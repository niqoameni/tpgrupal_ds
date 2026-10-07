package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;

import java.util.Objects;

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

    public FacturaVentaDetalle() {
    }

    public FacturaVentaDetalle(FacturaVenta factura, ListaPrecioArticulo listaPrecioArticulo, double cantidad, double precioUnitario) {
        this.factura = factura;
        this.listaPrecioArticulo = listaPrecioArticulo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.importeSubtotal = cantidad * precioUnitario;
    }

    public FacturaVentaDetalle(FacturaVenta factura, ListaPrecioArticulo listaPrecioArticulo, String descripcion, double cantidad, double precioUnitario, double porcentajeBonificacion) {
        this.factura = factura;
        this.listaPrecioArticulo = listaPrecioArticulo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.importeSubtotal = (cantidad * precioUnitario) * 1.21;
        this.porcentajeBonificacion = porcentajeBonificacion;
        this.importeNeto = cantidad * precioUnitario;
        this.importeIva = this.importeNeto * 0.21;
    }

    public FacturaVenta getFactura() {
        return factura;
    }

    public void setFactura(FacturaVenta factura) {
        this.factura = factura;
    }

    public ListaPrecioArticulo getListaPrecioArticulo() {
        return listaPrecioArticulo;
    }

    public void setListaPrecioArticulo(ListaPrecioArticulo listaPrecioArticulo) {
        this.listaPrecioArticulo = listaPrecioArticulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getImporteSubtotal() {
        return importeSubtotal;
    }

    public void setImporteSubtotal(double importeSubtotal) {
        this.importeSubtotal = importeSubtotal;
    }

    public double getPorcentajeBonificacion() {
        return porcentajeBonificacion;
    }

    public void setPorcentajeBonificacion(double porcentajeBonificacion) {
        this.porcentajeBonificacion = porcentajeBonificacion;
    }

    public double getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(double importeNeto) {
        this.importeNeto = importeNeto;
    }

    public double getImporteIva() {
        return importeIva;
    }

    public void setImporteIva(double importeIva) {
        this.importeIva = importeIva;
    }

    @Override
    public String toString() {
        return "FacturaVentaDetalle{" +
                "id=" + id +
                ", factura=" + factura.getNumero() +
                ", listaPrecioArticulo=" + listaPrecioArticulo.getId() +
                ", descripcion='" + descripcion + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", importeSubtotal=" + importeSubtotal +
                ", porcentajeBonificacion=" + porcentajeBonificacion +
                ", importeNeto=" + importeNeto +
                ", importeIva=" + importeIva +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacturaVentaDetalle that = (FacturaVentaDetalle) o;
        return Objects.equals(factura, that.factura) && Objects.equals(listaPrecioArticulo, that.listaPrecioArticulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factura, listaPrecioArticulo);
    }
}
