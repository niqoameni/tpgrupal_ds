package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "marca")
public class Marca extends AuditoriaApp{
    @Column(nullable = false)
    private String denominacion;

    @Column(nullable = false)
    private Integer codigo;

    public Marca(Usuario usuarioCarga, String denominacion, Integer codigo) {
        super(usuarioCarga);
        this.denominacion = denominacion;
        this.codigo = codigo;
    }
}
