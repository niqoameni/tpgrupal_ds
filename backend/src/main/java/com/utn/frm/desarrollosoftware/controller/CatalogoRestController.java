package com.utn.frm.desarrollosoftware.controller;

import com.utn.frm.desarrollosoftware.dto.ArticuloPrecioDTO;
import com.utn.frm.desarrollosoftware.entities.Cliente;
import com.utn.frm.desarrollosoftware.entities.CondicionIva;
import com.utn.frm.desarrollosoftware.entities.PuntoVenta;
import com.utn.frm.desarrollosoftware.entities.TipoMoneda;
import com.utn.frm.desarrollosoftware.repository.ClienteRepository;
import com.utn.frm.desarrollosoftware.repository.CondicionIvaRepository;
import com.utn.frm.desarrollosoftware.repository.ListaPrecioArticuloRepository;
import com.utn.frm.desarrollosoftware.repository.PuntoVentaRepository;
import com.utn.frm.desarrollosoftware.repository.TipoMonedaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CatalogoRestController {

    private final ClienteRepository clienteRepository;
    private final CondicionIvaRepository condicionIvaRepository;
    private final PuntoVentaRepository puntoVentaRepository;
    private final TipoMonedaRepository tipoMonedaRepository;
    private final ListaPrecioArticuloRepository listaPrecioArticuloRepository;

    public CatalogoRestController(
            ClienteRepository clienteRepository,
            CondicionIvaRepository condicionIvaRepository,
            PuntoVentaRepository puntoVentaRepository,
            TipoMonedaRepository tipoMonedaRepository,
            ListaPrecioArticuloRepository listaPrecioArticuloRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.condicionIvaRepository = condicionIvaRepository;
        this.puntoVentaRepository = puntoVentaRepository;
        this.tipoMonedaRepository = tipoMonedaRepository;
        this.listaPrecioArticuloRepository = listaPrecioArticuloRepository;
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Map<String, Object>>> getClientes() {
        List<Map<String, Object>> list = clienteRepository.findAll().stream().map(c -> Map.of(
                "id", (Object) c.getId(),
                "denominacion", c.getDenominacion(),
                "cuitCuil", c.getCuitCuil()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/condiciones-iva")
    public ResponseEntity<List<Map<String, Object>>> getCondicionesIva() {
        List<Map<String, Object>> list = condicionIvaRepository.findAll().stream().map(ci -> Map.of(
                "id", (Object) ci.getId(),
                "denominacion", ci.getDenominacion(),
                "codigoAfip", ci.getCodigoAfip()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/puntos-venta")
    public ResponseEntity<List<Map<String, Object>>> getPuntosVenta() {
        List<Map<String, Object>> list = puntoVentaRepository.findAll().stream().map(pv -> Map.of(
                "id", (Object) pv.getId(),
                "numero", pv.getNumero(),
                "descripcion", (pv.getDescripcion() != null ? pv.getDescripcion() : "PV " + pv.getNumero())
        )).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/tipos-moneda")
    public ResponseEntity<List<Map<String, Object>>> getTiposMoneda() {
        List<Map<String, Object>> list = tipoMonedaRepository.findAll().stream().map(tm -> Map.of(
                "id", (Object) tm.getId(),
                "denominacion", tm.getDenominacion(),
                "simbolo", tm.getSimbolo(),
                "codigoAfip", tm.getCodigoAfip()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/articulos")
    public ResponseEntity<List<ArticuloPrecioDTO>> getArticulos() {
        List<ArticuloPrecioDTO> articulos = listaPrecioArticuloRepository.findArticulosDisponibles();
        return ResponseEntity.ok(articulos);
    }
}
