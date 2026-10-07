package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "contacto")
public class Contacto extends EntityId{
    private String email;
    private String telefono;
    private String celular;

    public Contacto() {
    }

    public Contacto(String email, String telefono, String celular) {
        this.email = email;
        this.telefono = telefono;
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", celular='" + celular + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contacto contacto = (Contacto) o;
        return Objects.equals(email, contacto.email) && Objects.equals(celular, contacto.celular);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, celular);
    }
}
