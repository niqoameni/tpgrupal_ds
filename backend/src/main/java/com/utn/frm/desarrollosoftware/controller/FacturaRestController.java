package com.utn.frm.desarrollosoftware.controller;

import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;
import com.utn.frm.desarrollosoftware.dto.FacturaRequestDTO;
import com.utn.frm.desarrollosoftware.service.FacturaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.utn.frm.desarrollosoftware.util.PdfItextExporter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaRestController {

    private final FacturaService facturaService;

    public FacturaRestController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public ResponseEntity<List<FacturaReporteDTO>> obtenerFacturas(
            @RequestParam(required = false) Long numero,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam(required = false) Long condicionIvaId
    ) {
        List<FacturaReporteDTO> lista = facturaService.buscarFacturas(
                numero,
                cliente,
                fechaDesde,
                fechaHasta,
                condicionIvaId
        );
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarReportePdf(
            @RequestParam(required = false) Long numero,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaHasta,
            @RequestParam(required = false) Long condicionIvaId
    ) {
        List<FacturaReporteDTO> facturas = facturaService.buscarFacturas(numero, cliente, fechaDesde, fechaHasta, condicionIvaId);
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
