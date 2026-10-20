package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FacturaRepositoryImpl implements FacturaRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<FacturaReporteDTO> buscarFacturasFiltradas(
            Long numeroFactura,
            String cliente,
            Date fechaDesde,
            Date fechaHasta,
            Long condicionIvaId
    ) {
        StringBuilder jpql = new StringBuilder(
                "SELECT new com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO(" +
                        " f.id, f.numero, f.fechaEmision, COALESCE(c.denominacion, 'Consumidor Final'), " +
                        " ci.denominacion, pv.descripcion, f.importeTotal, COUNT(d)" +
                        ") " +
                        "FROM FacturaVenta f " +
                        "LEFT JOIN f.cliente c " +
                        "LEFT JOIN f.condicionIva ci " +
                        "LEFT JOIN f.puntoVenta pv " +
                        "LEFT JOIN f.detalles d " +
                        "WHERE f.fechaBaja IS NULL "
        );

        Map<String, Object> params = new HashMap<>();

        if (numeroFactura != null) {
            jpql.append("AND f.numero = :numeroFactura ");
            params.put("numeroFactura", numeroFactura);
        }
        if (cliente != null && !cliente.trim().isEmpty()) {
            jpql.append("AND (LOWER(c.denominacion) LIKE LOWER(:cliente) OR c.cuitCuil LIKE :cliente) ");
            params.put("cliente", "%" + cliente.trim() + "%");
        }
        if (fechaDesde != null) {
            jpql.append("AND f.fechaEmision >= :fechaDesde ");
            params.put("fechaDesde", fechaDesde);
        }
        if (fechaHasta != null) {
            jpql.append("AND f.fechaEmision <= :fechaHasta ");
            params.put("fechaHasta", fechaHasta);
        }
        if (condicionIvaId != null) {
            jpql.append("AND ci.id = :condicionIvaId ");
            params.put("condicionIvaId", condicionIvaId);
        }

        jpql.append("GROUP BY f.id, f.numero, f.fechaEmision, c.denominacion, ci.denominacion, pv.descripcion, f.importeTotal ");
        jpql.append("ORDER BY f.fechaEmision DESC, f.numero DESC");

        TypedQuery<FacturaReporteDTO> query = em.createQuery(jpql.toString(), FacturaReporteDTO.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }
}