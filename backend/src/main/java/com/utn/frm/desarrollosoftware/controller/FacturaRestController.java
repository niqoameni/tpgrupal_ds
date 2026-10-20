package com.utn.frm.desarrollosoftware.controller;

import com.utn.frm.desarrollosoftware.dto.FacturaDetalleRequestDTO;
import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;
import com.utn.frm.desarrollosoftware.dto.FacturaRequestDTO;
import com.utn.frm.desarrollosoftware.dto.FacturaResponseDTO;
import com.utn.frm.desarrollosoftware.service.FacturaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public ResponseEntity<List<FacturaReporteDTO>> obtenerFacturas(
            @RequestParam(required = false) Long numero,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam(required = false) Long condicionIvaId
    ) {
        List<FacturaReporteDTO> facturas = facturaService.buscarFacturas(
                numero,
                cliente,
                fechaDesde,
                fechaHasta,
                condicionIvaId
        );
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> obtenerFacturaPorId(@PathVariable Long id) {
        FacturaResponseDTO factura = facturaService.obtenerFacturaPorId(id);
        return ResponseEntity.ok(factura);
    }

    @PostMapping
    public ResponseEntity<FacturaResponseDTO> crearFactura(@RequestBody FacturaRequestDTO request) {
        FacturaResponseDTO nueva = facturaService.crearFactura(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PostMapping("/{id}/detalles")
    public ResponseEntity<FacturaResponseDTO> agregarDetalle(
            @PathVariable Long id,
            @RequestBody FacturaDetalleRequestDTO request
    ) {
        FacturaResponseDTO factura = facturaService.agregarDetalle(id, request);
        return ResponseEntity.ok(factura);
    }

    @DeleteMapping("/{id}/detalles/{detalleId}")
    public ResponseEntity<FacturaResponseDTO> eliminarDetalle(
            @PathVariable Long id,
            @PathVariable Long detalleId
    ) {
        FacturaResponseDTO factura = facturaService.eliminarDetalle(id, detalleId);
        return ResponseEntity.ok(factura);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> darDeBajaFactura(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "admin") String usuario
    ) {
        facturaService.darDeBajaLogica(id, usuario);
        return ResponseEntity.ok(Map.of("mensaje", "Factura dada de baja lógicamente con éxito"));
    }
}
