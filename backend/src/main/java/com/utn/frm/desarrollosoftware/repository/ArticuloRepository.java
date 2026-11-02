package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

    @Query("SELECT COALESCE(MAX(a.id), 0) FROM Articulo a")
    Long obtenerMaxId();

    boolean existsByCodigoIgnoreCase(String codigo);
}
