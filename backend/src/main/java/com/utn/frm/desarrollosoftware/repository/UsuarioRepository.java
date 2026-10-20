package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Usuario> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    public Optional<Usuario> findByUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) return Optional.empty();
        List<Usuario> list = em.createQuery("SELECT u FROM Usuario u WHERE u.usuario = :usuario", Usuario.class)
                .setParameter("usuario", usuario)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<Usuario> findFirst() {
        List<Usuario> list = em.createQuery("SELECT u FROM Usuario u ORDER BY u.id ASC", Usuario.class)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            em.persist(usuario);
            return usuario;
        } else {
            return em.merge(usuario);
        }
    }
}
