package com.utn.frm.desarrollosoftware.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloPrecioDTO {
    private Long listaPrecioArticuloId;
    private Long articuloId;
    private String codigo;
    private String denominacion;
    private String marca;
    private String rubro;
    private double precioVenta;
    private String listaPrecioNombre;
}
