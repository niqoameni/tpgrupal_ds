package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;

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
}
