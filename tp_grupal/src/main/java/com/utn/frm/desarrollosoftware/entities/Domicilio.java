package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "domicilio")
public class Domicilio extends EntityId{
    private String nombreCalle;
    private String numeroCalle;
}
