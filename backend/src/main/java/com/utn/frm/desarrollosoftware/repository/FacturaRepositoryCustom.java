package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;

import java.util.Date;
import java.util.List;

public interface FacturaRepositoryCustom {
    List<FacturaReporteDTO> buscarFacturasFiltradas(
            Long numeroFactura,
            String cliente,
            Date fechaDesde,
            Date fechaHasta,
            Long condicionIvaId
    );
}
