package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.Domicilio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomicilioRepository extends JpaRepository<Domicilio, Long> {
}

