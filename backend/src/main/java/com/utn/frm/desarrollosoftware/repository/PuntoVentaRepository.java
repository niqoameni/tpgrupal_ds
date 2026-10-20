package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.PuntoVenta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PuntoVentaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<PuntoVenta> findAll() {
        return em.createQuery("SELECT p FROM PuntoVenta p WHERE p.fechaBaja IS NULL ORDER BY p.numero ASC", PuntoVenta.class)
                .getResultList();
    }

    public Optional<PuntoVenta> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(PuntoVenta.class, id));
    }

    public PuntoVenta save(PuntoVenta puntoVenta) {
        if (puntoVenta.getId() == null) {
            em.persist(puntoVenta);
            return puntoVenta;
        } else {
            return em.merge(puntoVenta);
        }
    }
}
