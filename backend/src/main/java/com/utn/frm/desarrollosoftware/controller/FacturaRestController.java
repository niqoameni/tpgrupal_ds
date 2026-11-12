package com.utn.frm.desarrollosoftware.controller;

import com.utn.frm.desarrollosoftware.dto.ArticuloRequestDTO;
import com.utn.frm.desarrollosoftware.dto.ClienteRequestDTO;
import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;
import com.utn.frm.desarrollosoftware.dto.FacturaRequestDTO;
import com.utn.frm.desarrollosoftware.service.FacturaService;
import com.utn.frm.desarrollosoftware.util.PdfItextExporter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaRestController {

    private final FacturaService facturaService;

    public FacturaRestController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public ResponseEntity<Page<FacturaReporteDTO>> obtenerFacturas(
            @RequestParam(required = false) Long numero,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam(required = false) Long condicionIvaId,
            @RequestParam(required = false) Long listaPrecioArticuloId,
            @RequestParam(required = false) Boolean informadaArca,
            @PageableDefault(page = 0, size = 5) Pageable pageable
    ) {
        Page<FacturaReporteDTO> pagina = facturaService.buscarFacturasPaginadas(
                numero,
                cliente,
                fechaDesde,
                fechaHasta,
                condicionIvaId,
                listaPrecioArticuloId,
                informadaArca,
                pageable
        );
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/catalogos")
    public ResponseEntity<Map<String, Object>> obtenerCatalogos() {
        return ResponseEntity.ok(facturaService.obtenerCatalogos());
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Map<String, Object>>> buscarClientes(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String cuit
    ) {
        return ResponseEntity.ok(facturaService.buscarClientes(nombre, cuit));
    }

    @PostMapping("/clientes")
    public ResponseEntity<Map<String, Object>> crearCliente(@RequestBody ClienteRequestDTO request) {
        Map<String, Object> nuevoCliente = facturaService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    @GetMapping("/articulos/siguiente-codigo")
    public ResponseEntity<Map<String, String>> obtenerSiguienteCodigoArticulo() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("siguienteCodigo", facturaService.obtenerSiguienteCodigoArticulo());
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/articulos")
    public ResponseEntity<Map<String, Object>> crearArticulo(@RequestBody ArticuloRequestDTO request) {
        Map<String, Object> nuevoArticulo = facturaService.crearArticulo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoArticulo);
    }

    @GetMapping("/lista-precio-articulos")
    public ResponseEntity<List<Map<String, Object>>> obtenerArticulosPorListaPrecio(
            @RequestParam(required = false) Long listaPrecioId,
            @RequestParam(required = false) String articulo
    ) {
        return ResponseEntity.ok(facturaService.obtenerArticulosPorListaPrecio(listaPrecioId, articulo));
    }

    @GetMapping("/siguiente-numero")
    public ResponseEntity<Map<String, Long>> obtenerSiguienteNumero() {
        Map<String, Long> respuesta = new HashMap<>();
        respuesta.put("siguienteNumero", facturaService.obtenerSiguienteNumero());
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaReporteDTO> obtenerFacturaPorId(@PathVariable Long id) {
        FacturaReporteDTO factura = facturaService.obtenerPorId(id);
        return ResponseEntity.ok(factura);
    }

    @PostMapping("/{id}/solicitar-cae")
    public ResponseEntity<FacturaReporteDTO> solicitarCae(@PathVariable Long id) {
        FacturaReporteDTO factura = facturaService.solicitarCaePorId(id);
        return ResponseEntity.ok(factura);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarFacturaIndividualPdf(@PathVariable Long id) {
        FacturaReporteDTO factura = facturaService.obtenerPorId(id);
        byte[] pdfBytes = PdfItextExporter.exportarFacturaIndividualEnMemoria(factura);
        String nro = String.valueOf(factura.getNumeroFactura() != null ? factura.getNumeroFactura() : id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Factura_" + nro + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarReportePdf(
            @RequestParam(required = false) Long numero,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaHasta,
            @RequestParam(required = false) Long condicionIvaId,
            @RequestParam(required = false) Long listaPrecioArticuloId,
            @RequestParam(required = false) Boolean informadaArca
    ) {
        List<FacturaReporteDTO> facturas = facturaService.buscarFacturas(
                numero,
                cliente,
                fechaDesde,
                fechaHasta,
                condicionIvaId,
                listaPrecioArticuloId,
                informadaArca
        );
        byte[] pdfBytes = PdfItextExporter.exportarEnMemoria(facturas);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ventas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping
    public ResponseEntity<FacturaReporteDTO> crearFactura(@RequestBody FacturaRequestDTO request) {
        FacturaReporteDTO nuevaFactura = facturaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaReporteDTO> actualizarFactura(
            @PathVariable Long id,
            @RequestBody FacturaRequestDTO request
    ) {
        FacturaReporteDTO facturaActualizada = facturaService.actualizar(id, request);
        return ResponseEntity.ok(facturaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "admin") String usuario
    ) {
        facturaService.eliminar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
