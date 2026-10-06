package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "condicion_iva")
public class CondicionIva extends AuditoriaApp{
    @Column(nullable = false)
    private int codigoAfip;

    @Column(nullable = false)
    private String denominacion;
}
