package com.utn.frm.desarrollosoftware.service;

import com.utn.frm.desarrollosoftware.dto.*;
import com.utn.frm.desarrollosoftware.entities.*;
import com.utn.frm.desarrollosoftware.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class FacturaService {

    private static final Pattern CODIGO_ARTICULO_PATTERN = Pattern.compile("(\\d+)$");

    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final CondicionIvaRepository condicionIvaRepository;
    private final PuntoVentaRepository puntoVentaRepository;
    private final TipoMonedaRepository tipoMonedaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ListaPrecioRepository listaPrecioRepository;
    private final ListaPrecioArticuloRepository listaPrecioArticuloRepository;
    private final ArticuloRepository articuloRepository;
    private final MarcaRepository marcaRepository;
    private final RubroRepository rubroRepository;
    private final ContactoRepository contactoRepository;
    private final DomicilioRepository domicilioRepository;

    public FacturaService(
            FacturaRepository facturaRepository,
            ClienteRepository clienteRepository,
            CondicionIvaRepository condicionIvaRepository,
            PuntoVentaRepository puntoVentaRepository,
            TipoMonedaRepository tipoMonedaRepository,
            UsuarioRepository usuarioRepository,
            ListaPrecioRepository listaPrecioRepository,
            ListaPrecioArticuloRepository listaPrecioArticuloRepository,
            ArticuloRepository articuloRepository,
            MarcaRepository marcaRepository,
            RubroRepository rubroRepository,
            ContactoRepository contactoRepository,
            DomicilioRepository domicilioRepository
    ) {
        this.facturaRepository = facturaRepository;
        this.clienteRepository = clienteRepository;
        this.condicionIvaRepository = condicionIvaRepository;
        this.puntoVentaRepository = puntoVentaRepository;
        this.tipoMonedaRepository = tipoMonedaRepository;
        this.usuarioRepository = usuarioRepository;
        this.listaPrecioRepository = listaPrecioRepository;
        this.listaPrecioArticuloRepository = listaPrecioArticuloRepository;
        this.articuloRepository = articuloRepository;
        this.marcaRepository = marcaRepository;
        this.rubroRepository = rubroRepository;
        this.contactoRepository = contactoRepository;
        this.domicilioRepository = domicilioRepository;
    }

    public List<FacturaReporteDTO> buscarFacturas(
            Long numeroFactura,
            String cliente,
            Date fechaDesde,
            Date fechaHasta,
            Long condicionIvaId,
            Long listaPrecioArticuloId
    ) {
        log.info("Búsqueda de facturas - Nro: {}, Cliente: {}, Desde: {}, Hasta: {}, CondIva: {}, LPA: {}",
                numeroFactura, cliente, fechaDesde, fechaHasta, condicionIvaId, listaPrecioArticuloId);

        List<FacturaReporteDTO> resultados = facturaRepository.buscarFacturasFiltradas(
                numeroFactura,
                cliente,
                fechaDesde,
                fechaHasta,
                condicionIvaId,
                listaPrecioArticuloId
        );

        log.info("Búsqueda finalizada. Se encontraron {} registros.", resultados.size());
        return resultados;
    }

    @Transactional(readOnly = true)
    public Page<FacturaReporteDTO> buscarFacturasPaginadas(
            Long numeroFactura,
            String cliente,
            Date fechaDesde,
            Date fechaHasta,
            Long condicionIvaId,
            Long listaPrecioArticuloId,
            Pageable pageable
    ) {
        log.info("Búsqueda paginada de facturas - Nro: {}, Cliente: {}, Desde: {}, Hasta: {}, CondIva: {}, LPA: {}, Page: {}, Size: {}",
                numeroFactura, cliente, fechaDesde, fechaHasta, condicionIvaId, listaPrecioArticuloId,
                pageable != null ? pageable.getPageNumber() : 0,
                pageable != null ? pageable.getPageSize() : 0);

        Page<FacturaReporteDTO> pagina = facturaRepository.buscarFacturasFiltradasPaginadas(
                numeroFactura,
                cliente,
                fechaDesde,
                fechaHasta,
                condicionIvaId,
                listaPrecioArticuloId,
                pageable
        );

        log.info("Búsqueda paginada finalizada. Página {}/{} ({} registros de {} totales).",
                pagina.getNumber() + 1, pagina.getTotalPages(), pagina.getNumberOfElements(), pagina.getTotalElements());
        return pagina;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerCatalogos() {
        Map<String, Object> catalogos = new LinkedHashMap<>();

        List<Map<String, Object>> clientes = new ArrayList<>();
        for (Cliente c : clienteRepository.findAll()) {
            if (c.getFechaBaja() != null) continue;
            clientes.add(mapearClienteAMap(c));
        }
        catalogos.put("clientes", clientes);

        List<Map<String, Object>> condicionesIva = new ArrayList<>();
        for (CondicionIva ci : condicionIvaRepository.findAll()) {
            if (ci.getFechaBaja() != null) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", ci.getId());
            item.put("nombre", ci.getDenominacion());
            item.put("codigoAfip", ci.getCodigoAfip());
            item.put("detalle", "Código AFIP: " + ci.getCodigoAfip());
            condicionesIva.add(item);
        }
        catalogos.put("condicionesIva", condicionesIva);

        List<Map<String, Object>> puntosVenta = new ArrayList<>();
        for (PuntoVenta pv : puntoVentaRepository.findAll()) {
            if (pv.getFechaBaja() != null) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", pv.getId());
            item.put("nombre", pv.getDescripcion());
            item.put("numero", pv.getNumero());
            item.put("detalle", "Punto " + pv.getNumero() + (pv.getDomicilioComercial() != null ? " - " + pv.getDomicilioComercial() : ""));
            puntosVenta.add(item);
        }
        catalogos.put("puntosVenta", puntosVenta);

        List<Map<String, Object>> tiposMoneda = new ArrayList<>();
        for (TipoMoneda tm : tipoMonedaRepository.findAll()) {
            if (tm.getFechaBaja() != null) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", tm.getId());
            item.put("nombre", tm.getDenominacion() + " (" + tm.getSimbolo() + ")");
            item.put("denominacion", tm.getDenominacion());
            item.put("simbolo", tm.getSimbolo());
            item.put("detalle", "Cód: " + tm.getCodigoAfip());
            tiposMoneda.add(item);
        }
        catalogos.put("tiposMoneda", tiposMoneda);

        List<Map<String, Object>> listasPrecio = new ArrayList<>();
        for (ListaPrecio lp : listaPrecioRepository.findAll()) {
            if (lp.getFechaBaja() != null) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", lp.getId());
            String etiqueta = lp.getId() != null && lp.getId() == 2L
                    ? "Mayorista"
                    : (lp.getId() != null && lp.getId() == 1L ? "Minorista" : lp.getDenominacion());
            item.put("nombre", etiqueta + " - " + lp.getDenominacion());
            item.put("denominacion", lp.getDenominacion());
            item.put("codigo", lp.getCodigo());
            item.put("detalle", lp.getDenominacion());
            listasPrecio.add(item);
        }
        catalogos.put("listasPrecio", listasPrecio);

        List<Map<String, Object>> marcas = new ArrayList<>();
        for (Marca m : marcaRepository.findAll()) {
            if (m.getFechaBaja() != null) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.getId());
            item.put("nombre", m.getDenominacion());
            item.put("codigo", m.getCodigo());
            item.put("detalle", "Cód: " + m.getCodigo());
            marcas.add(item);
        }
        catalogos.put("marcas", marcas);

        List<Map<String, Object>> rubros = new ArrayList<>();
        for (Rubro r : rubroRepository.findAll()) {
            if (r.getFechaBaja() != null) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("nombre", r.getDenominacion());
            item.put("codigo", r.getCodigo());
            item.put("detalle", "Cód: " + r.getCodigo());
            rubros.add(item);
        }
        catalogos.put("rubros", rubros);

        catalogos.put("siguienteCodigoArticulo", obtenerSiguienteCodigoArticulo());
        catalogos.put("listaPrecioArticulos", obtenerArticulosPorListaPrecio(null, null));

        return catalogos;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> buscarClientes(String nombre, String cuit) {
        String filtroNombre = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        String filtroCuit = (cuit != null && !cuit.trim().isEmpty()) ? cuit.trim().replaceAll("[^0-9]", "") : null;

        List<Cliente> encontrados = clienteRepository.buscarClientes(filtroNombre, filtroCuit);
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Cliente c : encontrados) {
            if (c.getFechaBaja() != null) continue;
            resultado.add(mapearClienteAMap(c));
        }
        return resultado;
    }

    @Transactional
    public Map<String, Object> crearCliente(ClienteRequestDTO request) {
        if (request == null || request.getDenominacion() == null || request.getDenominacion().trim().isEmpty()) {
            throw new IllegalArgumentException("La denominación (nombre) del cliente es obligatoria.");
        }

        String cuitFinal = request.getCuitCuil() != null ? request.getCuitCuil().trim().replaceAll("[^0-9]", "") : "";

        if (cuitFinal.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el CUIT/CUIL del cliente.");
        }

        Usuario usuario = obtenerUsuarioParaAuditoria(request.getUsuario());

        Contacto contacto = new Contacto(
                request.getEmail() != null ? request.getEmail().trim() : null,
                request.getTelefono() != null ? request.getTelefono().trim() : null,
                request.getCelular() != null ? request.getCelular().trim() : null
        );
        contactoRepository.save(contacto);

        Domicilio domicilio = new Domicilio(
                request.getNombreCalle() != null ? request.getNombreCalle().trim() : null,
                request.getNumeroCalle() != null ? request.getNumeroCalle().trim() : null
        );
        domicilioRepository.save(domicilio);

        Cliente nuevoCliente = new Cliente(
                usuario,
                cuitFinal,
                request.getDenominacion().trim(),
                contacto,
                domicilio
        );

        Cliente guardado = clienteRepository.save(nuevoCliente);
        log.info("Nuevo cliente creado con éxito: ID={}, Denominación={}, CUIT={}",
                guardado.getId(), guardado.getDenominacion(), guardado.getCuitCuil());

        return mapearClienteAMap(guardado);
    }

    @Transactional(readOnly = true)
    public String obtenerSiguienteCodigoArticulo() {
        int maxCorrelativo = 0;
        for (Articulo art : articuloRepository.findAll()) {
            if (art.getCodigo() == null) continue;
            Matcher m = CODIGO_ARTICULO_PATTERN.matcher(art.getCodigo().trim());
            if (m.find()) {
                try {
                    int nro = Integer.parseInt(m.group(1));
                    if (nro > maxCorrelativo) {
                        maxCorrelativo = nro;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        Long maxId = articuloRepository.obtenerMaxId();
        if (maxId != null && maxId.intValue() > maxCorrelativo) {
            maxCorrelativo = maxId.intValue();
        }
        String candidato = String.format("ART-%03d", maxCorrelativo + 1);
        while (articuloRepository.existsByCodigoIgnoreCase(candidato)) {
            maxCorrelativo++;
            candidato = String.format("ART-%03d", maxCorrelativo + 1);
        }
        return candidato;
    }

    @Transactional
    public Map<String, Object> crearArticulo(ArticuloRequestDTO request) {
        if (request == null || request.getDenominacion() == null || request.getDenominacion().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre (denominación) del artículo es obligatorio.");
        }
        if (request.getMarcaId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una Marca para el artículo.");
        }
        if (request.getRubroId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un Rubro para el artículo.");
        }
        if (request.getPrecioMinorista() == null || request.getPrecioMinorista() <= 0) {
            throw new IllegalArgumentException("El precio Minorista debe ser mayor a 0.");
        }
        if (request.getPrecioMayorista() == null || request.getPrecioMayorista() <= 0) {
            throw new IllegalArgumentException("El precio Mayorista debe ser mayor a 0.");
        }

        Marca marca = marcaRepository.findById(request.getMarcaId())
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada con ID: " + request.getMarcaId()));

        Rubro rubro = rubroRepository.findById(request.getRubroId())
                .orElseThrow(() -> new IllegalArgumentException("Rubro no encontrado con ID: " + request.getRubroId()));

        Usuario usuario = obtenerUsuarioParaAuditoria(request.getUsuario());

        String codigo = (request.getCodigo() != null && !request.getCodigo().trim().isEmpty())
                ? request.getCodigo().trim()
                : obtenerSiguienteCodigoArticulo();

        if (articuloRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalArgumentException("Ya existe un artículo registrado con el código: " + codigo);
        }

        Articulo nuevoArticulo = new Articulo(usuario, rubro, codigo, request.getDenominacion().trim(), marca);
        Articulo articuloGuardado = articuloRepository.save(nuevoArticulo);

        // Lista Minorista (FK = 1)
        ListaPrecio listaMinorista = listaPrecioRepository.findById(1L)
                .orElseGet(() -> listaPrecioRepository.save(new ListaPrecio(usuario, "LP-01", "Lista General Minorista")));

        // Lista Mayorista (FK = 2)
        ListaPrecio listaMayorista = listaPrecioRepository.findById(2L)
                .orElseGet(() -> listaPrecioRepository.save(new ListaPrecio(usuario, "LP-02", "Lista General Mayorista")));

        ListaPrecioArticulo lpaMinorista = new ListaPrecioArticulo(
                usuario,
                listaMinorista,
                request.getPrecioMinorista(),
                articuloGuardado
        );
        ListaPrecioArticulo lpaMayorista = new ListaPrecioArticulo(
                usuario,
                listaMayorista,
                request.getPrecioMayorista(),
                articuloGuardado
        );

        lpaMinorista = listaPrecioArticuloRepository.save(lpaMinorista);
        lpaMayorista = listaPrecioArticuloRepository.save(lpaMayorista);

        log.info("Nuevo artículo creado: ID={}, Código={}, Denominación={}, LPA Minorista ID={}, LPA Mayorista ID={}",
                articuloGuardado.getId(), articuloGuardado.getCodigo(), articuloGuardado.getDenominacion(),
                lpaMinorista.getId(), lpaMayorista.getId());

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("id", articuloGuardado.getId());
        respuesta.put("codigo", articuloGuardado.getCodigo());
        respuesta.put("denominacion", articuloGuardado.getDenominacion());
        respuesta.put("marcaId", marca.getId());
        respuesta.put("marcaDenominacion", marca.getDenominacion());
        respuesta.put("rubroId", rubro.getId());
        respuesta.put("rubroDenominacion", rubro.getDenominacion());
        respuesta.put("precioMinorista", lpaMinorista.getPrecioVenta());
        respuesta.put("precioMayorista", lpaMayorista.getPrecioVenta());
        respuesta.put("lpaMinorista", mapearLpaAMap(lpaMinorista));
        respuesta.put("lpaMayorista", mapearLpaAMap(lpaMayorista));
        respuesta.put("siguienteCodigoArticulo", obtenerSiguienteCodigoArticulo());
        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerArticulosPorListaPrecio(Long listaPrecioId, String articulo) {
        String filtroArticulo = (articulo != null && !articulo.trim().isEmpty()) ? articulo.trim() : null;
        List<ListaPrecioArticulo> registros = listaPrecioArticuloRepository.buscarPorListaYArticulo(listaPrecioId, filtroArticulo);

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (ListaPrecioArticulo lpa : registros) {
            if (lpa.getFechaBaja() != null) continue;
            resultado.add(mapearLpaAMap(lpa));
        }
        return resultado;
    }

    private Map<String, Object> mapearLpaAMap(ListaPrecioArticulo lpa) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", lpa.getId());
        item.put("precioVenta", lpa.getPrecioVenta());
        if (lpa.getListaPrecio() != null) {
            item.put("listaPrecioId", lpa.getListaPrecio().getId());
            item.put("listaPrecioDenominacion", lpa.getListaPrecio().getDenominacion());
        }
        if (lpa.getArticulo() != null) {
            item.put("articuloId", lpa.getArticulo().getId());
            item.put("articuloCodigo", lpa.getArticulo().getCodigo());
            item.put("articuloDenominacion", lpa.getArticulo().getDenominacion());
        }
        return item;
    }

    private Map<String, Object> mapearClienteAMap(Cliente c) {
        Map<String, Object> item = new LinkedHashMap<>();
        String cuit = c.getCuitCuil() != null ? c.getCuitCuil().trim() : "";

        item.put("id", c.getId());
        item.put("nombre", c.getDenominacion());
        item.put("denominacion", c.getDenominacion());
        item.put("cuit", cuit);
        item.put("cuitCuil", cuit);
        item.put("detalle", "CUIT: " + cuit);

        if (c.getContacto() != null) {
            item.put("email", c.getContacto().getEmail());
            item.put("telefono", c.getContacto().getTelefono());
            item.put("celular", c.getContacto().getCelular());
        }
        if (c.getDomicilio() != null) {
            item.put("nombreCalle", c.getDomicilio().getNombreCalle());
            item.put("numeroCalle", c.getDomicilio().getNumeroCalle());
            String dir = ((c.getDomicilio().getNombreCalle() != null ? c.getDomicilio().getNombreCalle() : "") + " " +
                    (c.getDomicilio().getNumeroCalle() != null ? c.getDomicilio().getNumeroCalle() : "")).trim();
            item.put("domicilio", dir);
        }
        return item;
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

        Long idMoneda = request.getTipoMonedaId() != null ? request.getTipoMonedaId() : 1L;
        TipoMoneda tipoMoneda = tipoMonedaRepository.findById(idMoneda)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Moneda no encontrado con ID: " + idMoneda));

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
        if (detDto.getCantidad() == null || detDto.getCantidad() < 1) {
            throw new IllegalArgumentException("La cantidad no puede ser menor a 1.");
        }

        Double bonif = (detDto.getPorcentajeBonificacion() != null) ? detDto.getPorcentajeBonificacion() : 0.0;
        if (bonif < 0 || bonif > 99) {
            throw new IllegalArgumentException("El porcentaje de descuento no puede ser menor que 0 ni mayor a 99.");
        }

        ListaPrecioArticulo lpa = null;
        if (detDto.getListaPrecioArticuloId() != null) {
            lpa = listaPrecioArticuloRepository.findById(detDto.getListaPrecioArticuloId()).orElse(null);
        }
        if (lpa == null && detDto.getArticuloId() != null && detDto.getListaPrecioId() != null) {
            lpa = listaPrecioArticuloRepository.findByArticuloIdAndListaPrecioId(detDto.getArticuloId(), detDto.getListaPrecioId()).orElse(null);
        }
        if (lpa == null && detDto.getArticuloId() != null) {
            lpa = listaPrecioArticuloRepository.findByArticuloId(detDto.getArticuloId()).orElse(null);
        }

        if (lpa == null) {
            throw new IllegalArgumentException("No se encontró el artículo en la lista de precios especificada.");
        }

        double cantidad = detDto.getCantidad();
        String desc = (detDto.getDescripcion() != null && !detDto.getDescripcion().trim().isEmpty())
                ? detDto.getDescripcion()
                : (lpa.getArticulo() != null ? lpa.getArticulo().getDenominacion() : "Artículo");

        FacturaVentaDetalle detalle = new FacturaVentaDetalle(factura, lpa, desc, cantidad, bonif);
        detalle.setPrecioUnitario(lpa.getPrecioVenta());
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
        Long listaPrecioFacturaId = null;
        String listaPrecioFacturaDenominacion = null;

        if (f.getDetalles() != null) {
            for (FacturaVentaDetalle det : f.getDetalles()) {
                FacturaDetalleRequestDTO dDto = new FacturaDetalleRequestDTO();
                if (det.getListaPrecioArticulo() != null) {
                    ListaPrecioArticulo lpa = det.getListaPrecioArticulo();
                    dDto.setListaPrecioArticuloId(lpa.getId());
                    if (lpa.getArticulo() != null) {
                        dDto.setArticuloId(lpa.getArticulo().getId());
                        dDto.setArticuloDenominacion(lpa.getArticulo().getDenominacion());
                    }
                    if (lpa.getListaPrecio() != null) {
                        dDto.setListaPrecioId(lpa.getListaPrecio().getId());
                        if (listaPrecioFacturaId == null) {
                            listaPrecioFacturaId = lpa.getListaPrecio().getId();
                            listaPrecioFacturaDenominacion = lpa.getListaPrecio().getDenominacion();
                        }
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
            dto.setTipoMonedaDescripcion(f.getTipoMoneda().getDenominacion());
        }
        dto.setListaPrecioId(listaPrecioFacturaId != null ? listaPrecioFacturaId : 1L);
        dto.setListaPrecioDenominacion(listaPrecioFacturaDenominacion != null ? listaPrecioFacturaDenominacion : "Lista General Minorista");
        dto.setObservaciones(f.getObservaciones());
        dto.setEstado(f.getEstado());
        dto.setDetalles(detallesDto);

        return dto;
    }
}
