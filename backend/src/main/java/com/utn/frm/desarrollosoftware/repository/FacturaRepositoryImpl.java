package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
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
            Long condicionIvaId,
            Long listaPrecioArticuloId) {
        Map<String, Object> params = new HashMap<>();
        String whereClause = construirFiltrosWhere(
                numeroFactura,
                cliente,
                fechaDesde,
                fechaHasta,
                condicionIvaId,
                listaPrecioArticuloId,
                params
        );

        String jpql = "SELECT new com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO(" +
                " f.id, f.numero, f.fechaEmision, COALESCE(c.denominacion, 'Consumidor Final'), " +
                " ci.denominacion, pv.descripcion, f.importeTotal, COUNT(d), " +
                " f.cae, f.caeFechaVencimiento, f.resultadoAfip" +
                ") " +
                "FROM FacturaVenta f " +
                "LEFT JOIN f.cliente c " +
                "LEFT JOIN f.condicionIva ci " +
                "LEFT JOIN f.puntoVenta pv " +
                "LEFT JOIN f.detalles d " +
                whereClause +
                "GROUP BY f.id, f.numero, f.fechaEmision, c.denominacion, ci.denominacion, pv.descripcion, f.importeTotal, " +
                "f.cae, f.caeFechaVencimiento, f.resultadoAfip " +
                "ORDER BY f.fechaEmision DESC, f.numero DESC";

        TypedQuery<FacturaReporteDTO> query = em.createQuery(jpql, FacturaReporteDTO.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

    @Override
    public Page<FacturaReporteDTO> buscarFacturasFiltradasPaginadas(
            Long numeroFactura,
            String cliente,
            Date fechaDesde,
            Date fechaHasta,
            Long condicionIvaId,
            Long listaPrecioArticuloId,
            Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        String whereClause = construirFiltrosWhere(
                numeroFactura,
                cliente,
                fechaDesde,
                fechaHasta,
                condicionIvaId,
                listaPrecioArticuloId,
                params
        );

        String jpql = "SELECT new com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO(" +
                " f.id, f.numero, f.fechaEmision, COALESCE(c.denominacion, 'Consumidor Final'), " +
                " ci.denominacion, pv.descripcion, f.importeTotal, COUNT(d), " +
                " f.cae, f.caeFechaVencimiento, f.resultadoAfip" +
                ") " +
                "FROM FacturaVenta f " +
                "LEFT JOIN f.cliente c " +
                "LEFT JOIN f.condicionIva ci " +
                "LEFT JOIN f.puntoVenta pv " +
                "LEFT JOIN f.detalles d " +
                whereClause +
                "GROUP BY f.id, f.numero, f.fechaEmision, c.denominacion, ci.denominacion, pv.descripcion, f.importeTotal, " +
                "f.cae, f.caeFechaVencimiento, f.resultadoAfip " +
                "ORDER BY f.fechaEmision DESC, f.numero DESC";

        TypedQuery<FacturaReporteDTO> query = em.createQuery(jpql, FacturaReporteDTO.class);
        params.forEach(query::setParameter);

        if (pageable != null && pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<FacturaReporteDTO> content = query.getResultList();

        String countJpql = "SELECT COUNT(DISTINCT f.id) " +
                "FROM FacturaVenta f " +
                "LEFT JOIN f.cliente c " +
                "LEFT JOIN f.condicionIva ci " +
                whereClause;

        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
        params.forEach(countQuery::setParameter);
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable != null ? pageable : Pageable.unpaged(), total != null ? total : 0L);
    }

    private String construirFiltrosWhere(
            Long numeroFactura,
            String cliente,
            Date fechaDesde,
            Date fechaHasta,
            Long condicionIvaId,
            Long listaPrecioArticuloId,
            Map<String, Object> params) {
        StringBuilder where = new StringBuilder("WHERE f.fechaBaja IS NULL ");

        if (numeroFactura != null) {
            where.append("AND f.numero = :numeroFactura ");
            params.put("numeroFactura", numeroFactura);
        }
        if (cliente != null && !cliente.trim().isEmpty()) {
            where.append("AND (LOWER(c.denominacion) LIKE LOWER(:cliente) OR c.cuitCuil LIKE :cliente) ");
            params.put("cliente", "%" + cliente.trim() + "%");
        }
        if (fechaDesde != null) {
            where.append("AND f.fechaEmision >= :fechaDesde ");
            params.put("fechaDesde", fechaDesde);
        }
        if (fechaHasta != null) {
            // fechaEmision incluye hora: se compara contra el inicio del día siguiente
            // para que las facturas emitidas durante el día "hasta" también se incluyan.
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaHasta);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            where.append("AND f.fechaEmision < :fechaHasta ");
            params.put("fechaHasta", cal.getTime());
        }
        if (condicionIvaId != null) {
            where.append("AND ci.id = :condicionIvaId ");
            params.put("condicionIvaId", condicionIvaId);
        }
        if (listaPrecioArticuloId != null) {
            where.append("AND EXISTS (SELECT 1 FROM FacturaVentaDetalle dFiltro " +
                         "WHERE dFiltro.factura = f AND dFiltro.listaPrecioArticulo.id = :listaPrecioArticuloId) ");
            params.put("listaPrecioArticuloId", listaPrecioArticuloId);
        }

        return where.toString();
    }
}
