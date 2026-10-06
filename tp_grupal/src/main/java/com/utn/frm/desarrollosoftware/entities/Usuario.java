package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario extends EntityId{
    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private String clave;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;
}
