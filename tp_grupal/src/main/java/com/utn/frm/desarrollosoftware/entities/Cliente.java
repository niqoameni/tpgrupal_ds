package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "cliente")
public class Cliente extends AuditoriaApp{
    @Column(nullable = false)
    private String cuitCuil;

    @Column(nullable = false)
    private String denominacion;

    @OneToOne
    @JoinColumn(nullable = false)
    private Contacto contacto;

    @OneToOne
    @JoinColumn(nullable = false)
    private Domicilio domicilio;

    public Cliente(Usuario usuarioCarga, String cuitCuil, String denominacion, Contacto contacto, Domicilio domicilio) {
        super(usuarioCarga);
        this.cuitCuil = cuitCuil;
        this.denominacion = denominacion;
        this.contacto = contacto;
        this.domicilio = domicilio;
    }

    public String getCuitCuil() {
        return cuitCuil;
    }

    public void setCuitCuil(String cuitCuil) {
        this.cuitCuil = cuitCuil;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", cuitCuil='" + cuitCuil + '\'' +
                ", denominacion='" + denominacion + '\'' +
                ", contacto=" + contacto +
                ", domicilio=" + domicilio +
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
        Cliente cliente = (Cliente) o;
        return Objects.equals(cuitCuil, cliente.cuitCuil) && Objects.equals(denominacion, cliente.denominacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cuitCuil, denominacion);
    }
}
