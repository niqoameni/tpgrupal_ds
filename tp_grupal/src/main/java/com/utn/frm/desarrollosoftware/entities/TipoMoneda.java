package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "tipo_moneda")
public class TipoMoneda extends AuditoriaApp{
    @Column(nullable = false)
    private String codigoAfip;

    @Column(nullable = false)
    private String denominacion;

    @Column(nullable = false)
    private String simbolo;

    public TipoMoneda() {
    }

    public TipoMoneda(Usuario usuarioCarga, String codigoAfip, String denominacion, String simbolo) {
        super(usuarioCarga);
        this.codigoAfip = codigoAfip;
        this.denominacion = denominacion;
        this.simbolo = simbolo;
    }

    public String getCodigoAfip() {
        return codigoAfip;
    }

    public void setCodigoAfip(String codigoAfip) {
        this.codigoAfip = codigoAfip;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    @Override
    public String toString() {
        return "TipoMoneda{" +
                "id=" + id +
                ", codigoAfip='" + codigoAfip + '\'' +
                ", denominacion='" + denominacion + '\'' +
                ", simbolo='" + simbolo + '\'' +
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
        TipoMoneda that = (TipoMoneda) o;
        return Objects.equals(codigoAfip, that.codigoAfip) && Objects.equals(denominacion, that.denominacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoAfip, denominacion);
    }
}



