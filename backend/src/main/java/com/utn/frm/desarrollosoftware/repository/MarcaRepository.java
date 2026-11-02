package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
}

