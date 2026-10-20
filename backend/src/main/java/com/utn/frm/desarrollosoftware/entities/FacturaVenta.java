package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "factura_venta")
public class FacturaVenta extends  AuditoriaApp{
    private Long numero;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaEmision;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CondicionIva condicionIva;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TipoMoneda tipoMoneda;

    @ManyToOne
    @JoinColumn(nullable = false)
    private PuntoVenta puntoVenta;

    private double importeCobrado;
    private double importeSaldo;

    @Column(nullable = false)
    private double importeTotal;

    private String cae;
    @Temporal(TemporalType.DATE)
    private Date caeFechaVencimiento;
    private String resultadoAfip;
    private String motivoRechazo;

    @Column(nullable = false)
    private String estado;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAnulacion;
    private String observaciones;

    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaVentaDetalle> detalles = new ArrayList<>();

    public FacturaVenta(Usuario usuarioCarga, Cliente cliente, CondicionIva condicionIva, TipoMoneda tipoMoneda, PuntoVenta puntoVenta) {
        super(usuarioCarga);
        this.fechaEmision = new Date();
        this.cliente = cliente;
        this.condicionIva = condicionIva;
        this.tipoMoneda = tipoMoneda;
        this.puntoVenta = puntoVenta;
        this.estado = "EMITIDA";
    }

    public FacturaVenta(Usuario usuarioCarga, Long numero, Cliente cliente, CondicionIva condicionIva, TipoMoneda tipoMoneda, PuntoVenta puntoVenta, double importeSaldo, String cae, Date caeFechaVencimiento, String resultadoAfip, Date fechaAnulacion, String observaciones) {
        super(usuarioCarga);
        this.numero = numero;
        this.fechaEmision = new Date();
        this.cliente = cliente;
        this.condicionIva = condicionIva;
        this.tipoMoneda = tipoMoneda;
        this.puntoVenta = puntoVenta;
        this.importeSaldo = importeSaldo;
        this.cae = cae;
        this.caeFechaVencimiento = caeFechaVencimiento;
        this.resultadoAfip = resultadoAfip;
        this.fechaAnulacion = fechaAnulacion;
        this.estado = (fechaAnulacion != null) ? "ANULADA" : "EMITIDA";
        this.observaciones = observaciones;
    }

    public void calcularTotal() {
        double total = 0.0;
        double cobrado = 0.0;

        if (this.detalles != null) {
            for (FacturaVentaDetalle detalle : this.detalles) {
                total += (detalle.getImporteNeto() + detalle.getImporteIva());
                cobrado += detalle.getImporteSubtotal();
            }
        }

        this.importeTotal = total;
        this.importeCobrado = cobrado;
    }

    public void addDetalle(FacturaVentaDetalle detalle){
        this.detalles.add(detalle);
        detalle.setFactura(this);
        calcularTotal();
    }

    public void removeDetalle(FacturaVentaDetalle detalle){
        this.detalles.remove(detalle);
        detalle.setFactura(null);
        calcularTotal();
    }
}
