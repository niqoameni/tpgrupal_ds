package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.ListaPrecioArticulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListaPrecioArticuloRepository extends JpaRepository<ListaPrecioArticulo, Long> {
    Optional<ListaPrecioArticulo> findByArticuloId(Long articuloId);
}
