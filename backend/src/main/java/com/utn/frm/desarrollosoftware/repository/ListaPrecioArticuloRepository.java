package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.ListaPrecioArticulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaPrecioArticuloRepository extends JpaRepository<ListaPrecioArticulo, Long> {
    Optional<ListaPrecioArticulo> findByArticuloId(Long articuloId);
    List<ListaPrecioArticulo> findByListaPrecioId(Long listaPrecioId);
    Optional<ListaPrecioArticulo> findByArticuloIdAndListaPrecioId(Long articuloId, Long listaPrecioId);

    @Query("SELECT lpa FROM ListaPrecioArticulo lpa " +
           "JOIN lpa.articulo a " +
           "JOIN lpa.listaPrecio lp " +
           "WHERE lpa.fechaBaja IS NULL AND a.fechaBaja IS NULL " +
           "AND (:listaPrecioId IS NULL OR lp.id = :listaPrecioId) " +
           "AND (:articulo IS NULL OR LOWER(a.denominacion) LIKE LOWER(CONCAT('%', :articulo, '%')) " +
           "     OR LOWER(a.codigo) LIKE LOWER(CONCAT('%', :articulo, '%'))) " +
           "ORDER BY a.codigo ASC, lp.id ASC")
    List<ListaPrecioArticulo> buscarPorListaYArticulo(
            @Param("listaPrecioId") Long listaPrecioId,
            @Param("articulo") String articulo
    );
}
