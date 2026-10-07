package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "condicion_iva")
public class CondicionIva extends AuditoriaApp{
    @Column(nullable = false)
    private int codigoAfip;

    @Column(nullable = false)
    private String denominacion;

    public CondicionIva(Usuario usuarioCarga, String denominacion, int codigoAfip) {
        super(usuarioCarga);
        this.denominacion = denominacion;
        this.codigoAfip = codigoAfip;
    }

    public int getCodigoAfip() {
        return codigoAfip;
    }

    public void setCodigoAfip(int codigoAfip) {
        this.codigoAfip = codigoAfip;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    @Override
    public String toString() {
        return "CondicionIva{" +
                "id=" + id +
                ", denominacion='" + denominacion + '\'' +
                ", codigoAfip=" + codigoAfip +
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
        CondicionIva that = (CondicionIva) o;
        return codigoAfip == that.codigoAfip && Objects.equals(denominacion, that.denominacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoAfip, denominacion);
    }
}
