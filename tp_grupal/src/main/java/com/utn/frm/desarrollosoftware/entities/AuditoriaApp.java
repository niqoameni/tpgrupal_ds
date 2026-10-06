package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;

import java.util.Date;

@MappedSuperclass
public class AuditoriaApp {
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaAlta;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaBaja;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaModificacion;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected Usuario usuarioCarga;

    @ManyToOne
    protected Usuario usuarioBaja;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected Usuario usuarioModificacion;


}
