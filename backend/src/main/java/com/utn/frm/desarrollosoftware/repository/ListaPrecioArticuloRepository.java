package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.dto.ArticuloPrecioDTO;
import com.utn.frm.desarrollosoftware.entities.ListaPrecioArticulo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ListaPrecioArticuloRepository {

    @PersistenceContext
    private EntityManager em;

    public List<ArticuloPrecioDTO> findArticulosDisponibles() {
        String jpql = "SELECT new com.utn.frm.desarrollosoftware.dto.ArticuloPrecioDTO(" +
                      " lpa.id, a.id, a.codigo, a.denominacion, " +
                      " m.denominacion, r.denominacion, lpa.precioVenta, lp.denominacion) " +
                      "FROM ListaPrecioArticulo lpa " +
                      "JOIN lpa.articulo a " +
                      "JOIN lpa.listaPrecio lp " +
                      "LEFT JOIN a.marca m " +
                      "LEFT JOIN a.rubro r " +
                      "WHERE lpa.fechaBaja IS NULL AND a.fechaBaja IS NULL " +
                      "ORDER BY a.denominacion ASC";
        return em.createQuery(jpql, ArticuloPrecioDTO.class).getResultList();
    }

    public Optional<ListaPrecioArticulo> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(ListaPrecioArticulo.class, id));
    }

    public Optional<ListaPrecioArticulo> findByArticuloId(Long articuloId) {
        if (articuloId == null) return Optional.empty();
        List<ListaPrecioArticulo> list = em.createQuery(
                "SELECT lpa FROM ListaPrecioArticulo lpa WHERE lpa.articulo.id = :articuloId AND lpa.fechaBaja IS NULL ORDER BY lpa.id ASC",
                ListaPrecioArticulo.class)
                .setParameter("articuloId", articuloId)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public ListaPrecioArticulo save(ListaPrecioArticulo lpa) {
        if (lpa.getId() == null) {
            em.persist(lpa);
            return lpa;
        } else {
            return em.merge(lpa);
        }
    }
}
