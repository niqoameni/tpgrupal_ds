package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "lista_precio")
public class ListaPrecio extends AuditoriaApp{
    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String denominacion;

    public ListaPrecio(Usuario usuarioCarga, String codigo, String denominacion) {
        super(usuarioCarga);
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    @Override
    public String toString() {
        return "ListaPrecio{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", denominacion='" + denominacion + '\'' +
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
        ListaPrecio that = (ListaPrecio) o;
        return Objects.equals(codigo, that.codigo) && Objects.equals(denominacion, that.denominacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, denominacion);
    }
}
