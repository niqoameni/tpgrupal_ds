package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.CondicionIva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CondicionIvaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<CondicionIva> findAll() {
        return em.createQuery("SELECT c FROM CondicionIva c WHERE c.fechaBaja IS NULL ORDER BY c.codigoAfip ASC", CondicionIva.class)
                .getResultList();
    }

    public Optional<CondicionIva> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(CondicionIva.class, id));
    }

    public CondicionIva save(CondicionIva condicionIva) {
        if (condicionIva.getId() == null) {
            em.persist(condicionIva);
            return condicionIva;
        } else {
            return em.merge(condicionIva);
        }
    }
}
