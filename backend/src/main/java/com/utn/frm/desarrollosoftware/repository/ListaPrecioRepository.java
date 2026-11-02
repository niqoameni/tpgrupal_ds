package com.utn.frm.desarrollosoftware.repository;

import com.utn.frm.desarrollosoftware.entities.ListaPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaPrecioRepository extends JpaRepository<ListaPrecio, Long> {
}

