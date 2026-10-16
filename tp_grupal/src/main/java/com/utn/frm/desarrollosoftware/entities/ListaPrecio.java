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
@Table(name = "lista_precio")
public class ListaPrecio extends AuditoriaApp{
    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String denominacion;

    public ListaPrecio(Usuario usuarioCarga, String codigo, String denominacion) {
        super(usuarioCarga);
        this.codigo = codigo;
        this.denominacion = denominacion;
    }
}
