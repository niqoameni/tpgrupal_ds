package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.CondicionIva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CondicionIvaRepository extends JpaRepository<CondicionIva, Long> {
}
