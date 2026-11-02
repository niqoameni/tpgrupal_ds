package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE c.fechaBaja IS NULL " +
           "AND (:nombre IS NULL OR LOWER(c.denominacion) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:cuit IS NULL OR c.cuitCuil LIKE CONCAT('%', :cuit, '%')) " +
           "ORDER BY c.denominacion ASC")
    List<Cliente> buscarClientes(
            @Param("nombre") String nombre,
            @Param("cuit") String cuit
    );
}
