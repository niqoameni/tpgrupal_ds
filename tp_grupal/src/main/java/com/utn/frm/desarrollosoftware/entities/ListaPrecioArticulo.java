package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "lista_precio_articulo")
public class ListaPrecioArticulo extends AuditoriaApp{
    @ManyToOne
    @JoinColumn(nullable = false)
    private ListaPrecio listaPrecio;

    @Column(nullable = false)
    private double precioVenta;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Articulo articulo;
}
