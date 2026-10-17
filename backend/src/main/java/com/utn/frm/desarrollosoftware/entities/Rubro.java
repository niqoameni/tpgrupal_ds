package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "rubro")
public class Rubro extends AuditoriaApp{
    @Column(nullable = false)
    private String denominacion;

    @Column(nullable = false)
    private Integer codigo;

    public Rubro(Usuario usuarioCarga, String denominacion, Integer codigo) {
        super(usuarioCarga);
        this.denominacion = denominacion;
        this.codigo = codigo;
    }
}
