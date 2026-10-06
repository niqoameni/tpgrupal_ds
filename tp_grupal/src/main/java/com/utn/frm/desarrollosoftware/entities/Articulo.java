package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "articulo")
public class Articulo extends AuditoriaApp{
    @ManyToOne
    private Rubro rubro;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String denominacion;

    @ManyToOne
    private Marca marca;
}
