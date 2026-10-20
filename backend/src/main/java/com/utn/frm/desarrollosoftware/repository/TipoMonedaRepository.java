package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.TipoMoneda;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TipoMonedaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<TipoMoneda> findAll() {
        return em.createQuery("SELECT t FROM TipoMoneda t WHERE t.fechaBaja IS NULL ORDER BY t.denominacion ASC", TipoMoneda.class)
                .getResultList();
    }

    public Optional<TipoMoneda> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(TipoMoneda.class, id));
    }

    public TipoMoneda save(TipoMoneda tipoMoneda) {
        if (tipoMoneda.getId() == null) {
            em.persist(tipoMoneda);
            return tipoMoneda;
        } else {
            return em.merge(tipoMoneda);
        }
    }
}
