package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface FacturaRepositoryCustom {
    List<FacturaReporteDTO> buscarFacturasFiltradas(
            Long numeroFactura,
            String cliente,
            Date fechaDesde,
            Date fechaHasta,
            Long condicionIvaId,
            Long listaPrecioArticuloId
    );

    Page<FacturaReporteDTO> buscarFacturasFiltradasPaginadas(
            Long numeroFactura,
            String cliente,
            Date fechaDesde,
            Date fechaHasta,
            Long condicionIvaId,
            Long listaPrecioArticuloId,
            Pageable pageable
    );
}
