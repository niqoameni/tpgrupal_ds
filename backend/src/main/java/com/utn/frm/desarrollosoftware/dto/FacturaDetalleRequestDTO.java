package com.utn.frm.desarrollosoftware.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDetalleRequestDTO {
    private Long articuloId;
    private Long listaPrecioArticuloId;
    private String descripcion;
    private Double cantidad;
    private Double precioUnitario;
    private Double porcentajeBonificacion;
    private Double porcentajeIva;
}
