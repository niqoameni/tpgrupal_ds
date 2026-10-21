package com.utn.frm.desarrollosoftware.service;

import com.utn.frm.desarrollosoftware.dto.*;
import com.utn.frm.desarrollosoftware.entities.*;
import com.utn.frm.desarrollosoftware.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public Long obtenerSiguienteNumero() {
        Long maxNumero = facturaRepository.obtenerMaxNumero();
        Long maxId = facturaRepository.obtenerMaxId();
        long max = Math.max(maxNumero != null ? maxNumero : 0L, maxId != null ? maxId : 0L);
        if (max <= 0) {
            return 1001L;
        }
        return max + 1;
    }

    @Transactional(readOnly = true)
    public FacturaReporteDTO obtenerPorId(Long id) {
        log.info("Buscando factura por ID: {}", id);
        FacturaVenta factura = facturaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Factura no encontrada con ID: " + id));

        return mapearAReporteDTO(factura);
    }

    @Transactional
    public FacturaReporteDTO crear(FacturaRequestDTO request) {
        log.info("Iniciando creación de factura...");

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + request.getClienteId()));

        CondicionIva condicionIva = condicionIvaRepository.findById(request.getCondicionIvaId())
                .orElseThrow(() -> new IllegalArgumentException("Condición de IVA no encontrada con ID: " + request.getCondicionIvaId()));

        PuntoVenta puntoVenta = puntoVentaRepository.findById(request.getPuntoVentaId())
                .orElseThrow(() -> new IllegalArgumentException("Punto de Venta no encontrado con ID: " + request.getPuntoVentaId()));

        TipoMoneda tipoMoneda = tipoMonedaRepository.findById(request.getTipoMonedaId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Moneda no encontrado con ID: " + request.getTipoMonedaId()));

        Usuario usuario = obtenerUsuarioParaAuditoria(request.getUsuario());

        Long numero = request.getNumero();
        if (numero == null || numero <= 0) {
            numero = obtenerSiguienteNumero();
        }

        FacturaVenta factura = new FacturaVenta(usuario, cliente, condicionIva, tipoMoneda, puntoVenta);
        factura.setNumero(numero);
        factura.setFechaEmision(request.getFechaEmision() != null ? request.getFechaEmision() : new Date());
        factura.setObservaciones(request.getObservaciones());
        factura.setEstado(request.getEstado() != null ? request.getEstado() : "EMITIDA");

        if (request.getDetalles() != null && !request.getDetalles().isEmpty()) {
            for (FacturaDetalleRequestDTO detDto : request.getDetalles()) {
                FacturaVentaDetalle detalle = crearDetalleDesdeDto(detDto, factura);
                factura.addDetalle(detalle);
            }
        }
        factura.calcularTotal();

        FacturaVenta guardada = facturaRepository.save(factura);
        log.info("Factura N° {} persistida con éxito bajo ID: {}", guardada.getNumero(), guardada.getId());

        return mapearAReporteDTO(guardada);
    }

    @Transactional
    public FacturaReporteDTO actualizar(Long id, FacturaRequestDTO request) {
        log.info("Actualizando factura ID: {}", id);

        FacturaVenta factura = facturaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Factura no encontrada con ID: " + id));

        if (factura.getFechaBaja() != null) {
            throw new IllegalStateException("No se puede editar una factura dada de baja.");
        }

        if (request.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + request.getClienteId()));
            factura.setCliente(cliente);
        }

        if (request.getCondicionIvaId() != null) {
            CondicionIva condicionIva = condicionIvaRepository.findById(request.getCondicionIvaId())
                    .orElseThrow(() -> new IllegalArgumentException("Condición de IVA no encontrada con ID: " + request.getCondicionIvaId()));
            factura.setCondicionIva(condicionIva);
        }

        if (request.getPuntoVentaId() != null) {
            PuntoVenta puntoVenta = puntoVentaRepository.findById(request.getPuntoVentaId())
                    .orElseThrow(() -> new IllegalArgumentException("Punto de Venta no encontrado con ID: " + request.getPuntoVentaId()));
            factura.setPuntoVenta(puntoVenta);
        }

        if (request.getTipoMonedaId() != null) {
            TipoMoneda tipoMoneda = tipoMonedaRepository.findById(request.getTipoMonedaId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de Moneda no encontrado con ID: " + request.getTipoMonedaId()));
            factura.setTipoMoneda(tipoMoneda);
        }

        if (request.getEstado() != null && !request.getEstado().trim().isEmpty()) {
            factura.setEstado(request.getEstado());
        }
        if (request.getObservaciones() != null) {
            factura.setObservaciones(request.getObservaciones());
        }

        Usuario usuario = obtenerUsuarioParaAuditoria(request.getUsuario());
        factura.setUsuarioModificacion(usuario);
        factura.setFechaModificacion(new Date());

        if (request.getDetalles() != null) {
            factura.getDetalles().clear();
            for (FacturaDetalleRequestDTO detDto : request.getDetalles()) {
                FacturaVentaDetalle detalle = crearDetalleDesdeDto(detDto, factura);
                factura.addDetalle(detalle);
            }
        }
        factura.calcularTotal();

        FacturaVenta guardada = facturaRepository.save(factura);
        log.info("Factura ID {} actualizada con éxito en BD. Nuevo total: {}", guardada.getId(), guardada.getImporteTotal());

        return mapearAReporteDTO(guardada);
    }

    @Transactional
    public void eliminar(Long id, String username) {
        log.info("Procesando anulación lógica para factura ID: {}", id);

        FacturaVenta factura = facturaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Factura no encontrada con ID: " + id));

        if (factura.getFechaBaja() != null) {
            log.warn("La factura ID {} ya se encontraba anulada.", id);
            return;
        }

        Usuario usuario = obtenerUsuarioParaAuditoria(username);
        factura.setFechaBaja(usuario);
        factura.setEstado("ANULADA");
        factura.setFechaAnulacion(new Date());

        facturaRepository.save(factura);
        log.info("Factura ID {} anulada lógicamente.", id);
    }

    private FacturaVentaDetalle crearDetalleDesdeDto(FacturaDetalleRequestDTO detDto, FacturaVenta factura) {
        ListaPrecioArticulo lpa = null;
        if (detDto.getListaPrecioArticuloId() != null) {
            lpa = listaPrecioArticuloRepository.findById(detDto.getListaPrecioArticuloId()).orElse(null);
        }
        if (lpa == null && detDto.getArticuloId() != null) {
            lpa = listaPrecioArticuloRepository.findByArticuloId(detDto.getArticuloId()).orElse(null);
        }

        if (lpa == null) {
            throw new IllegalArgumentException("No se encontró el artículo ni la lista de precios especificada.");
        }

        double cantidad = (detDto.getCantidad() != null && detDto.getCantidad() > 0) ? detDto.getCantidad() : 1.0;
        Double bonif = (detDto.getPorcentajeBonificacion() != null) ? detDto.getPorcentajeBonificacion() : 0.0;
        String desc = (detDto.getDescripcion() != null && !detDto.getDescripcion().trim().isEmpty())
                ? detDto.getDescripcion()
                : (lpa.getArticulo() != null ? lpa.getArticulo().getDenominacion() : "Artículo");

        FacturaVentaDetalle detalle = new FacturaVentaDetalle(factura, lpa, desc, cantidad, bonif);
        if (detDto.getPrecioUnitario() != null && detDto.getPrecioUnitario() > 0) {
            detalle.setPrecioUnitario(detDto.getPrecioUnitario());
        }
        detalle.calcularImportes();
        return detalle;
    }

    private Usuario obtenerUsuarioParaAuditoria(String username) {
        if (username != null && !username.trim().isEmpty()) {
            Optional<Usuario> u = usuarioRepository.findByUsuario(username);
            if (u.isPresent()) return u.get();
        }
        return usuarioRepository.findFirst()
                .orElseGet(() -> usuarioRepository.save(new Usuario("admin", "admin", "Admin", "Sistema")));
    }

    private FacturaReporteDTO mapearAReporteDTO(FacturaVenta f) {
        List<FacturaDetalleRequestDTO> detallesDto = new ArrayList<>();
        if (f.getDetalles() != null) {
            for (FacturaVentaDetalle det : f.getDetalles()) {
                FacturaDetalleRequestDTO dDto = new FacturaDetalleRequestDTO();
                if (det.getListaPrecioArticulo() != null) {
                    dDto.setListaPrecioArticuloId(det.getListaPrecioArticulo().getId());
                    if (det.getListaPrecioArticulo().getArticulo() != null) {
                        dDto.setArticuloId(det.getListaPrecioArticulo().getArticulo().getId());
                    }
                }
                dDto.setDescripcion(det.getDescripcion());
                dDto.setCantidad(det.getCantidad());
                dDto.setPrecioUnitario(det.getPrecioUnitario());
                dDto.setPorcentajeBonificacion(det.getPorcentajeBonificacion());
                dDto.setPorcentajeIva(det.getImporteIva());
                detallesDto.add(dDto);
            }
        }

        FacturaReporteDTO dto = new FacturaReporteDTO(
                f.getId(),
                f.getNumero(),
                f.getFechaEmision(),
                f.getCliente() != null ? f.getCliente().getDenominacion() : "Consumidor Final",
                f.getCondicionIva() != null ? f.getCondicionIva().getDenominacion() : "-",
                f.getPuntoVenta() != null ? f.getPuntoVenta().getDescripcion() : "-",
                f.getImporteTotal(),
                (long) detallesDto.size()
        );

        if (f.getCliente() != null) {
            dto.setClienteId(f.getCliente().getId());
            dto.setClienteCuit(f.getCliente().getCuitCuil());
        }
        if (f.getCondicionIva() != null) {
            dto.setCondicionIvaId(f.getCondicionIva().getId());
        }
        if (f.getPuntoVenta() != null) {
            dto.setPuntoVentaId(f.getPuntoVenta().getId());
        }
        if (f.getTipoMoneda() != null) {
            dto.setTipoMonedaId(f.getTipoMoneda().getId());
            dto.setTipoMonedaDescripcion(f.getTipoMoneda().getDescripcion());
        }
        dto.setObservaciones(f.getObservaciones());
        dto.setEstado(f.getEstado());
        dto.setDetalles(detallesDto);

        return dto;
    }
}