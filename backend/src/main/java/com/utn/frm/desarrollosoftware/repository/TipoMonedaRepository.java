package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.TipoMoneda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoMonedaRepository extends JpaRepository<TipoMoneda, Long> {
}
