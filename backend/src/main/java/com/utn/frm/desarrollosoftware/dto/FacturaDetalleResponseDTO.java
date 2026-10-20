package com.utn.frm.desarrollosoftware.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDetalleResponseDTO {
    private Long id;
    private Long listaPrecioArticuloId;
    private Long articuloId;
    private String codigoArticulo;
    private String denominacionArticulo;
    private String descripcion;
    private double cantidad;
    private double precioUnitario;
    private Double porcentajeBonificacion;
    private double importeNeto;
    private double importeIva;
    private double importeSubtotal;
}
