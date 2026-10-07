package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;

import java.util.Objects;

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

    public ListaPrecioArticulo(Usuario usuarioCarga, ListaPrecio listaPrecio, double precioVenta, Articulo articulo) {
        super(usuarioCarga);
        this.listaPrecio = listaPrecio;
        this.precioVenta = precioVenta;
        this.articulo = articulo;
    }

    public ListaPrecio getListaPrecio() {
        return listaPrecio;
    }

    public void setListaPrecio(ListaPrecio listaPrecio) {
        this.listaPrecio = listaPrecio;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    @Override
    public String toString() {
        return "ListaPrecioArticulo{" +
                "id=" + id +
                ", listaPrecio=" + listaPrecio +
                ", precioVenta=" + precioVenta +
                ", articulo=" + articulo +
                ", fechaAlta=" + fechaAlta +
                ", fechaBaja=" + fechaBaja +
                ", fechaModificacion=" + fechaModificacion +
                ", usuarioCarga=" + usuarioCarga +
                ", usuarioBaja=" + usuarioBaja +
                ", usuarioModificacion=" + usuarioModificacion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListaPrecioArticulo that = (ListaPrecioArticulo) o;
        return Objects.equals(listaPrecio, that.listaPrecio) && Objects.equals(articulo, that.articulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listaPrecio, articulo);
    }
}
