package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

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

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<FacturaVentaDetalle> detalles;

}
