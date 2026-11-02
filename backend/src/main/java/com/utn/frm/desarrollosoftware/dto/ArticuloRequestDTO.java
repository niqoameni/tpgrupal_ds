package com.utn.frm.desarrollosoftware.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloRequestDTO {
    private String codigo;
    private String denominacion;
    private Long marcaId;
    private Long rubroId;
    private Double precioMinorista;
    private Double precioMayorista;
    private String usuario;
}

