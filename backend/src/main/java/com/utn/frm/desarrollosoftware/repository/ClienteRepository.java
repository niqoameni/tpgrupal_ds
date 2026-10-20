package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Cliente> findAll() {
        return em.createQuery("SELECT c FROM Cliente c WHERE c.fechaBaja IS NULL ORDER BY c.denominacion ASC", Cliente.class)
                .getResultList();
    }

    public Optional<Cliente> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(Cliente.class, id));
    }

    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            em.persist(cliente);
            return cliente;
        } else {
            return em.merge(cliente);
        }
    }
}
