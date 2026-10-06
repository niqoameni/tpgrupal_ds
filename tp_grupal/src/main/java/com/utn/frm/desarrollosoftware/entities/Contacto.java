package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "contacto")
public class Contacto extends EntityId{
    private String email;
    private String telefono;
    private String celular;
}
