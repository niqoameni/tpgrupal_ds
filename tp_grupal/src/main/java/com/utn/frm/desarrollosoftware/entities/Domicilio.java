package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "domicilio")
public class Domicilio extends EntityId{
    private String nombreCalle;
    private String numeroCalle;

    public Domicilio() {
    }

    public Domicilio(String nombreCalle, String numeroCalle) {
        this.nombreCalle = nombreCalle;
        this.numeroCalle = numeroCalle;
    }

    public String getNombreCalle() {
        return nombreCalle;
    }

    public void setNombreCalle(String nombreCalle) {
        this.nombreCalle = nombreCalle;
    }

    public String getNumeroCalle() {
        return numeroCalle;
    }

    public void setNumeroCalle(String numeroCalle) {
        this.numeroCalle = numeroCalle;
    }

    @Override
    public String toString() {
        return "Domicilio{" +
                "id=" + id +
                ", nombreCalle='" + nombreCalle + '\'' +
                ", numeroCalle='" + numeroCalle + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domicilio domicilio = (Domicilio) o;
        return Objects.equals(nombreCalle, domicilio.nombreCalle) && Objects.equals(numeroCalle, domicilio.numeroCalle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreCalle, numeroCalle);
    }
}
