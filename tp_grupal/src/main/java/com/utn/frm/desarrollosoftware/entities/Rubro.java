package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "rubro")
public class Rubro extends AuditoriaApp{
    @Column(nullable = false)
    private String denominacion;

    @Column(nullable = false)
    private Integer codigo;
}
