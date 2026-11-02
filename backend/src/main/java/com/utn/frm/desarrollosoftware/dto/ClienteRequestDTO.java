package com.utn.frm.desarrollosoftware.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {
    private String denominacion;
    private String cuitCuil;
    private String email;
    private String telefono;
    private String celular;
    private String nombreCalle;
    private String numeroCalle;
    private String usuario;
}
