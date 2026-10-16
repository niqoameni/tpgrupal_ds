package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@MappedSuperclass
public abstract class AuditoriaApp extends EntityId{
    @Setter(AccessLevel.NONE)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaAlta = new Date();

    @Setter(AccessLevel.NONE)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaBaja;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaModificacion = new Date();

    @Setter(AccessLevel.NONE)
    @ManyToOne
    @JoinColumn(nullable = false)
    protected Usuario usuarioCarga;

    @ManyToOne
    protected Usuario usuarioBaja;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected Usuario usuarioModificacion;

    public AuditoriaApp(Usuario usuarioCarga) {
        this.usuarioCarga = usuarioCarga;
        this.usuarioModificacion = this.usuarioCarga;
    }

    public void setFechaBaja(Usuario usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
        this.fechaBaja = new Date();
        this.fechaModificacion = this.fechaBaja;
        this.usuarioBaja = usuarioModificacion;
    }

    @ToString.Include(name = "usuarioCarga")
    public String getUserUsuarioCarga(){
        return (usuarioCarga != null) ? usuarioCarga.getUsuario() : null;
    }
}
