package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "lista_precio_articulo")
public class ListaPrecioArticulo extends AuditoriaApp{
    @ToString.Include
    @ManyToOne
    @JoinColumn(nullable = false)
    private ListaPrecio listaPrecio;

    @ToString.Include
    @Column(nullable = false)
    private double precioVenta;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Articulo articulo;

    public ListaPrecioArticulo(Usuario usuarioCarga, ListaPrecio listaPrecio, double precioVenta, Articulo articulo) {
        super(usuarioCarga);
        this.listaPrecio = listaPrecio;
        this.precioVenta = precioVenta;
        this.articulo = articulo;
    }

    @ToString.Include(name = "codigoArticulo")
    public String getCodigoArticulo(){
        return (articulo != null) ? articulo.getCodigo() : null;
    }

    @ToString.Include(name = "denominacionArticulo")
    public String getDenominacionArticulo(){
        return (articulo != null) ? articulo.getDenominacion() : null;
    }
}
