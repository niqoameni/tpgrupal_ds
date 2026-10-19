package com.utn.frm.desarrollosoftware.service;

import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;
import com.utn.frm.desarrollosoftware.repository.FacturaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FacturaService {
    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository){
        this.facturaRepository = facturaRepository;
    }

    public List<FacturaReporteDTO> buscarFacturas(Date fechaDesde, Date fechaHasta, String estado, Double montoMinimo) {
        log.info("Ejecutando búsqueda de facturas con filtros - Desde: {}, Hasta: {}, Estado: {}, MontoMin: {}", fechaDesde, fechaHasta, estado, montoMinimo);

        List<FacturaReporteDTO> resultados = facturaRepository.buscarFacturasFiltradas(
                fechaDesde,
                fechaHasta,
                estado,
                montoMinimo
        );

        log.info("Búsqueda finalizada. Se encontraron {} registros.", resultados.size());

        return resultados;
    }

}
