package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "articulo")
public class Articulo extends AuditoriaApp{
    @ManyToOne
    private Rubro rubro;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String denominacion;

    @ManyToOne
    private Marca marca;

    public Articulo(Usuario usuarioCarga, String codigo, String denominacion) {
        super(usuarioCarga);
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public Articulo(Usuario usuarioCarga, Rubro rubro, String codigo, String denominacion, Marca marca) {
        super(usuarioCarga);
        this.rubro = rubro;
        this.codigo = codigo;
        this.denominacion = denominacion;
        this.marca = marca;
    }
}
