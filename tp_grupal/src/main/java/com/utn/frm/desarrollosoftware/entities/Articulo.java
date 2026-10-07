package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "articulo")
public class Articulo extends AuditoriaApp{
    @ManyToOne
    private Rubro rubro;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String denominacion;

    @ManyToOne
    private Marca marca;

    public Articulo(Usuario usuarioCarga, String codigo, String denominacion) {
        super(usuarioCarga);
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public Articulo(Usuario usuarioCarga, Rubro rubro, String codigo, String denominacion, Marca marca) {
        super(usuarioCarga);
        this.rubro = rubro;
        this.codigo = codigo;
        this.denominacion = denominacion;
        this.marca = marca;
    }

    public Rubro getRubro() {
        return rubro;
    }

    public void setRubro(Rubro rubro) {
        this.rubro = rubro;
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

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    @Override
    public String toString() {
        return "Articulo{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", denominacion='" + denominacion + '\'' +
                ", rubro=" + rubro +
                ", marca=" + marca +
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
        Articulo articulo = (Articulo) o;
        return Objects.equals(codigo, articulo.codigo) && Objects.equals(denominacion, articulo.denominacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, denominacion);
    }
}
