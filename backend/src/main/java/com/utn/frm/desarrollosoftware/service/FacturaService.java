package com.utn.frm.desarrollosoftware.service;

import com.utn.frm.desarrollosoftware.dto.*;
import com.utn.frm.desarrollosoftware.entities.*;
import com.utn.frm.desarrollosoftware.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final CondicionIvaRepository condicionIvaRepository;
    private final PuntoVentaRepository puntoVentaRepository;
    private final TipoMonedaRepository tipoMonedaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ListaPrecioArticuloRepository listaPrecioArticuloRepository;

    public FacturaService(
            FacturaRepository facturaRepository,
            ClienteRepository clienteRepository,
            CondicionIvaRepository condicionIvaRepository,
            PuntoVentaRepository puntoVentaRepository,
            TipoMonedaRepository tipoMonedaRepository,
            UsuarioRepository usuarioRepository,
            ListaPrecioArticuloRepository listaPrecioArticuloRepository
    ) {
        this.facturaRepository = facturaRepository;
        this.clienteRepository = clienteRepository;
        this.condicionIvaRepository = condicionIvaRepository;
        this.puntoVentaRepository = puntoVentaRepository;
        this.tipoMonedaRepository = tipoMonedaRepository;
        this.usuarioRepository = usuarioRepository;
        this.listaPrecioArticuloRepository = listaPrecioArticuloRepository;
    }

    public List<FacturaReporteDTO> buscarFacturas(
            Long numeroFactura,
            String cliente,
            Date fechaDesde,
            Date fechaHasta,
            Long condicionIvaId
    ) {
        log.info("Búsqueda de facturas - Nro: {}, Cliente: {}, Desde: {}, Hasta: {}, CondIva: {}",
                numeroFactura, cliente, fechaDesde, fechaHasta, condicionIvaId);

        List<FacturaReporteDTO> resultados = facturaRepository.buscarFacturasFiltradas(
                numeroFactura,
                cliente,
                fechaDesde,
                fechaHasta,
                condicionIvaId
        );

        log.info("Búsqueda finalizada. Se encontraron {} registros.", resultados.size());
        return resultados;
    }

    public FacturaResponseDTO obtenerFacturaPorId(Long id) {
        FacturaVenta factura = facturaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Factura no encontrada con ID: " + id));
        return mapearAFacturaResponseDTO(factura);
    }

    @Transactional
    public FacturaResponseDTO crearFactura(FacturaRequestDTO request) {
        Usuario usuario = obtenerUsuarioParaAuditoria(request.getUsuario());

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + request.getClienteId()));

        CondicionIva condicionIva = condicionIvaRepository.findById(request.getCondicionIvaId())
                .orElseThrow(() -> new IllegalArgumentException("Condición de IVA no encontrada con ID: " + request.getCondicionIvaId()));

        PuntoVenta puntoVenta = puntoVentaRepository.findById(request.getPuntoVentaId())
                .orElseThrow(() -> new IllegalArgumentException("Punto de Venta no encontrado con ID: " + request.getPuntoVentaId()));

        TipoMoneda tipoMoneda = tipoMonedaRepository.findById(request.getTipoMonedaId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Moneda no encontrado con ID: " + request.getTipoMonedaId()));

        Long numero = request.getNumero();
        if (numero == null || numero <= 0) {
            numero = facturaRepository.obtenerSiguienteNumero(puntoVenta.getId());
        }

        FacturaVenta factura = new FacturaVenta(usuario, cliente, condicionIva, tipoMoneda, puntoVenta);
        factura.setNumero(numero);
        if (request.getFechaEmision() != null) {
            factura.setFechaEmision(request.getFechaEmision());
        }
        factura.setObservaciones(request.getObservaciones());

        if (request.getDetalles() != null) {
            for (FacturaDetalleRequestDTO detDto : request.getDetalles()) {
                FacturaVentaDetalle detalle = crearDetalleDesdeDto(detDto, factura);
                factura.addDetalle(detalle);
            }
        }

        FacturaVenta guardada = facturaRepository.save(factura);
        return mapearAFacturaResponseDTO(guardada);
    }

    @Transactional
    public FacturaResponseDTO agregarDetalle(Long facturaId, FacturaDetalleRequestDTO request) {
        FacturaVenta factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new NoSuchElementException("Factura no encontrada con ID: " + facturaId));

        if (factura.getFechaBaja() != null) {
            throw new IllegalStateException("No se pueden agregar artículos a una factura dada de baja.");
        }

        FacturaVentaDetalle nuevoDetalle = crearDetalleDesdeDto(request, factura);
        factura.addDetalle(nuevoDetalle);

        FacturaVenta guardada = facturaRepository.save(factura);
        return mapearAFacturaResponseDTO(guardada);
    }

    @Transactional
    public FacturaResponseDTO eliminarDetalle(Long facturaId, Long detalleId) {
        FacturaVenta factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new NoSuchElementException("Factura no encontrada con ID: " + facturaId));

        if (factura.getFechaBaja() != null) {
            throw new IllegalStateException("No se pueden modificar artículos de una factura dada de baja.");
        }

        FacturaVentaDetalle detalleAEliminar = factura.getDetalles().stream()
                .filter(d -> Objects.equals(d.getId(), detalleId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Detalle no encontrado con ID: " + detalleId));

        factura.removeDetalle(detalleAEliminar);

        FacturaVenta guardada = facturaRepository.save(factura);
        return mapearAFacturaResponseDTO(guardada);
    }

    @Transactional
    public void darDeBajaLogica(Long facturaId, String username) {
        FacturaVenta factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new NoSuchElementException("Factura no encontrada con ID: " + facturaId));

        if (factura.getFechaBaja() != null) {
            log.warn("La factura ID {} ya se encontraba dada de baja.", facturaId);
            return;
        }

        Usuario usuario = obtenerUsuarioParaAuditoria(username);
        factura.setFechaBaja(usuario);
        factura.setEstado("ANULADA");

        facturaRepository.save(factura);
        log.info("Baja lógica completada con éxito para factura ID: {}", facturaId);
    }

    private FacturaVentaDetalle crearDetalleDesdeDto(FacturaDetalleRequestDTO detDto, FacturaVenta factura) {
        ListaPrecioArticulo lpa = null;
        if (detDto.getListaPrecioArticuloId() != null) {
            lpa = listaPrecioArticuloRepository.findById(detDto.getListaPrecioArticuloId())
                    .orElse(null);
        }
        if (lpa == null && detDto.getArticuloId() != null) {
            lpa = listaPrecioArticuloRepository.findByArticuloId(detDto.getArticuloId())
                    .orElse(null);
        }

        if (lpa == null) {
            throw new IllegalArgumentException("No se encontró el artículo o lista de precios asociado.");
        }

        double cantidad = (detDto.getCantidad() != null && detDto.getCantidad() > 0) ? detDto.getCantidad() : 1.0;
        Double bonif = (detDto.getPorcentajeBonificacion() != null) ? detDto.getPorcentajeBonificacion() : 0.0;
        String desc = (detDto.getDescripcion() != null && !detDto.getDescripcion().trim().isEmpty())
                ? detDto.getDescripcion()
                : (lpa.getArticulo() != null ? lpa.getArticulo().getDenominacion() : "Artículo");

        FacturaVentaDetalle detalle = new FacturaVentaDetalle(factura, lpa, desc, cantidad, bonif);
        if (detDto.getPrecioUnitario() != null && detDto.getPrecioUnitario() > 0) {
            detalle.setPrecioUnitario(detDto.getPrecioUnitario());
            detalle.calcularImportes();
        }
        return detalle;
    }

    private Usuario obtenerUsuarioParaAuditoria(String username) {
        if (username != null && !username.trim().isEmpty()) {
            Optional<Usuario> u = usuarioRepository.findByUsuario(username);
            if (u.isPresent()) return u.get();
        }
        return usuarioRepository.findFirst()
                .orElseGet(() -> {
                    Usuario admin = new Usuario("admin", "admin", "Administrador", "Sistema");
                    return usuarioRepository.save(admin);
                });
    }

    public FacturaResponseDTO mapearAFacturaResponseDTO(FacturaVenta f) {
        FacturaResponseDTO dto = new FacturaResponseDTO();
        dto.setId(f.getId());
        dto.setNumero(f.getNumero());
        dto.setFechaEmision(f.getFechaEmision());

        if (f.getCliente() != null) {
            dto.setClienteId(f.getCliente().getId());
            dto.setClienteDenominacion(f.getCliente().getDenominacion());
            dto.setClienteCuit(f.getCliente().getCuitCuil());
        }

        if (f.getCondicionIva() != null) {
            dto.setCondicionIvaId(f.getCondicionIva().getId());
            dto.setCondicionIvaDenominacion(f.getCondicionIva().getDenominacion());
        }

        if (f.getPuntoVenta() != null) {
            dto.setPuntoVentaId(f.getPuntoVenta().getId());
            dto.setPuntoVentaDescripcion(f.getPuntoVenta().getDescripcion());
        }

        if (f.getTipoMoneda() != null) {
            dto.setTipoMonedaId(f.getTipoMoneda().getId());
            dto.setTipoMonedaDenominacion(f.getTipoMoneda().getDenominacion());
            dto.setTipoMonedaSimbolo(f.getTipoMoneda().getSimbolo());
        }

        dto.setImporteCobrado(f.getImporteCobrado());
        dto.setImporteSaldo(f.getImporteSaldo());
        dto.setImporteTotal(f.getImporteTotal());
        dto.setEstado(f.getEstado());
        dto.setObservaciones(f.getObservaciones());

        if (f.getDetalles() != null) {
            List<FacturaDetalleResponseDTO> detallesDto = f.getDetalles().stream().map(d -> {
                FacturaDetalleResponseDTO det = new FacturaDetalleResponseDTO();
                det.setId(d.getId());
                if (d.getListaPrecioArticulo() != null) {
                    det.setListaPrecioArticuloId(d.getListaPrecioArticulo().getId());
                    if (d.getListaPrecioArticulo().getArticulo() != null) {
                        det.setArticuloId(d.getListaPrecioArticulo().getArticulo().getId());
                        det.setCodigoArticulo(d.getListaPrecioArticulo().getArticulo().getCodigo());
                        det.setDenominacionArticulo(d.getListaPrecioArticulo().getArticulo().getDenominacion());
                    }
                }
                det.setDescripcion(d.getDescripcion());
                det.setCantidad(d.getCantidad());
                det.setPrecioUnitario(d.getPrecioUnitario());
                det.setPorcentajeBonificacion(d.getPorcentajeBonificacion());
                det.setImporteNeto(d.getImporteNeto());
                det.setImporteIva(d.getImporteIva());
                det.setImporteSubtotal(d.getImporteSubtotal());
                return det;
            }).collect(Collectors.toList());
            dto.setDetalles(detallesDto);
        }

        return dto;
    }
}
