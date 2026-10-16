package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "factura_detalle")
public class FacturaVentaDetalle extends EntityId{
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private FacturaVenta factura;

    @ToString.Include
    @ManyToOne
    @JoinColumn(nullable = false)
    private ListaPrecioArticulo listaPrecioArticulo;

    @ToString.Include
    private String descripcion;

    @ToString.Include
    @Column(nullable = false)
    private double cantidad;

    @ToString.Include
    @Column(nullable = false)
    private double precioUnitario;

    @ToString.Include
    @Column(nullable = false)
    private double importeSubtotal;

    @ToString.Include
    private Double porcentajeBonificacion;
    @ToString.Include
    private double importeNeto;
    @ToString.Include
    private double importeIva;

    public FacturaVentaDetalle(FacturaVenta factura, ListaPrecioArticulo listaPrecioArticulo, double cantidad) {
        this.factura = factura;
        this.listaPrecioArticulo = listaPrecioArticulo;
        this.cantidad = cantidad;
        this.precioUnitario = (listaPrecioArticulo != null) ? listaPrecioArticulo.getPrecioVenta() : 0.0;
        this.porcentajeBonificacion = 0.0;
        calcularImportes();
    }

    public FacturaVentaDetalle(FacturaVenta factura, ListaPrecioArticulo listaPrecioArticulo, String descripcion, double cantidad, Double porcentajeBonificacion) {
        this.factura = factura;
        this.listaPrecioArticulo = listaPrecioArticulo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = (listaPrecioArticulo != null) ? listaPrecioArticulo.getPrecioVenta() : 0.0;
        this.porcentajeBonificacion = (porcentajeBonificacion != null) ? porcentajeBonificacion : 0.0;
        calcularImportes();
    }

    public void calcularImportes() {
        double bruto = this.cantidad * this.precioUnitario;
        double descuento = bruto * (this.porcentajeBonificacion / 100.0);
        this.importeNeto = bruto - descuento;
        this.importeIva = this.importeNeto * 0.21;
        this.importeSubtotal = this.importeNeto + this.importeIva;
    }

    @ToString.Include(name = "numeroFactura")
    public Long getNumeroFactura(){
        return (factura != null) ? factura.getNumero() : null;
    }
}
