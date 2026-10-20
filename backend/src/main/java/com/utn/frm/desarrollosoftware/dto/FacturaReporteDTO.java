package com.utn.frm.desarrollosoftware.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaReporteDTO {
    private Long id;
    private Long numeroFactura;
    private Date fechaEmision;
    private String clienteDenominacion;
    private String condicionIva;
    private String puntoVentaDescripcion;
    private Double importeFinal;
    private Long cantidadItems;

    // Constructor sin ID para compatibilidad con código legacy previo
    public FacturaReporteDTO(Long numeroFactura, Date fechaEmision, String clienteDenominacion,
                             String condicionIva, String puntoVentaDescripcion,
                             Double importeFinal, Long cantidadItems) {
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.clienteDenominacion = clienteDenominacion;
        this.condicionIva = condicionIva;
        this.puntoVentaDescripcion = puntoVentaDescripcion;
        this.importeFinal = importeFinal;
        this.cantidadItems = cantidadItems;
    }
}
