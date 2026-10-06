package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;

@MappedSuperclass
public class AuditoriaApp {
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaAlta;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaBaja;

    @Column(nullable = false)
    protected Date fechaModificacion;
}
