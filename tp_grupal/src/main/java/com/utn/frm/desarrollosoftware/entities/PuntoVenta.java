package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "punto_venta")
public class PuntoVenta extends AuditoriaApp{
    @Column(nullable = false)
    private int numero;

    private String descripcion;
    private String tipoEmision;
    private String domicilioComercial;

}
