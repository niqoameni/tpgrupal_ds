package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.FacturaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<FacturaVenta, Long>, FacturaRepositoryCustom {

    @Query("SELECT COALESCE(MAX(f.numero), 0) + 1 FROM FacturaVenta f WHERE (:pvId IS NULL OR f.puntoVenta.id = :pvId)")
    Long obtenerSiguienteNumero(@Param("pvId") Long puntoVentaId);

    @Query("SELECT COALESCE(MAX(f.numero), 0) FROM FacturaVenta f")
    Long obtenerMaxNumero();

    @Query("SELECT COALESCE(MAX(f.id), 0) FROM FacturaVenta f")
    Long obtenerMaxId();
}

