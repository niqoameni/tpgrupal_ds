package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "domicilio")
public class Domicilio extends EntityId{
    private String nombreCalle;
    private String numeroCalle;
}
