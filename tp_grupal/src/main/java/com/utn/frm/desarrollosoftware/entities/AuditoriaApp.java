package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class AuditoriaApp extends EntityId{
    @Setter(AccessLevel.NONE)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaAlta = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaBaja;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaModificacion = fechaAlta;

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

}
