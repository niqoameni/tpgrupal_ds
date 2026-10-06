package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "factura_detalle")
public class FacturaVentaDetalle extends EntityId{
    @ManyToOne
    @JoinColumn(nullable = false)
    private FacturaVenta factura;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ListaPrecioArticulo listaPrecioArticulo;

    private String descripcion;

    @Column(nullable = false)
    private double cantidad;

    @Column(nullable = false)
    private double precioUnitario;

    @Column(nullable = false)
    private double importeSubtotal;

    private double porcentajeBonificacion;
    private double importeNeto;
    private double importeIva;

}
