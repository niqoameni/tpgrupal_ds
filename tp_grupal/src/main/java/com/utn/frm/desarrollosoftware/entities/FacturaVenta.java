package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private List<FacturaVentaDetalle> detalles = new ArrayList<>();

    public FacturaVenta() {
    }

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
        this.estado = "EMITIDA";
        this.fechaAnulacion = fechaAnulacion;
        this.observaciones = observaciones;
    }

    public void calcularTotal() {
        double total = 0.0;
        double cobrado = 0.0;
        for (FacturaVentaDetalle detalle : this.detalles){
            total = total + (detalle.getImporteNeto() + detalle.getImporteIva());
            cobrado = cobrado + (detalle.getImporteSubtotal());
        }
        this.importeTotal = total;
        this.importeCobrado = cobrado;
    }

    public void addDetalle(FacturaVentaDetalle detalle){
        this.detalles.add(detalle);
        detalle.setFactura(this);
        calcularTotal();
    }

    public Long getNumero() {
        return numero;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public CondicionIva getCondicionIva() {
        return condicionIva;
    }

    public TipoMoneda getTipoMoneda() {
        return tipoMoneda;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public double getImporteCobrado() {
        return importeCobrado;
    }

    public double getImporteSaldo() {
        return importeSaldo;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public String getCae() {
        return cae;
    }

    public Date getCaeFechaVencimiento() {
        return caeFechaVencimiento;
    }

    public String getResultadoAfip() {
        return resultadoAfip;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public String getEstado() {
        return estado;
    }

    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public List<FacturaVentaDetalle> getDetalles() {
        return detalles;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setCondicionIva(CondicionIva condicionIva) {
        this.condicionIva = condicionIva;
    }

    public void setTipoMoneda(TipoMoneda tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public void setImporteCobrado(double importeCobrado) {
        this.importeCobrado = importeCobrado;
    }

    public void setImporteSaldo(double importeSaldo) {
        this.importeSaldo = importeSaldo;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public void setCae(String cae) {
        this.cae = cae;
    }

    public void setCaeFechaVencimiento(Date caeFechaVencimiento) {
        this.caeFechaVencimiento = caeFechaVencimiento;
    }

    public void setResultadoAfip(String resultadoAfip) {
        this.resultadoAfip = resultadoAfip;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "FacturaVenta{" +
                "id=" + id +
                ", numero=" + numero +
                ", fechaEmision=" + fechaEmision +
                ", cliente=" + cliente.getDenominacion() + " (" + cliente.getCuitCuil() + ")" +
                ", condicionIva=" + condicionIva.getDenominacion() +
                ", tipoMoneda=" + tipoMoneda.getDenominacion() +
                ", puntoVenta=" + puntoVenta.getNumero() +
                ", importeCobrado=" + importeCobrado +
                ", importeSaldo=" + importeSaldo +
                ", importeTotal=" + importeTotal +
                ", cae='" + cae + '\'' +
                ", caeFechaVencimiento=" + caeFechaVencimiento +
                ", resultadoAfip='" + resultadoAfip + '\'' +
                ", motivoRechazo='" + motivoRechazo + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaAnulacion=" + fechaAnulacion +
                ", observaciones='" + observaciones + '\'' +
                ", detalles=" + detalles.size() +
                ", fechaAlta=" + fechaAlta +
                ", fechaBaja=" + fechaBaja +
                ", fechaModificacion=" + fechaModificacion +
                ", usuarioCarga=" + usuarioCarga.getUsuario() +
                ", usuarioBaja=" + (usuarioBaja != null ? usuarioBaja.getUsuario() : " ") +
                ", usuarioModificacion=" + usuarioModificacion.getUsuario() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacturaVenta that = (FacturaVenta) o;
        return Objects.equals(numero, that.numero) && Objects.equals(cliente, that.cliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, cliente);
    }
}
