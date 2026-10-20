package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.PuntoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuntoVentaRepository extends JpaRepository<PuntoVenta, Long> {
}
