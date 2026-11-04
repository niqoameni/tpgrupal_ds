package com.utn.frm.desarrollosoftware.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

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

    // Campos detallados adicionales para edición y consulta completa
    private Long clienteId;
    private String clienteCuit;
    private Long condicionIvaId;
    private Long puntoVentaId;
    private Long tipoMonedaId;
    private String tipoMonedaDescripcion;
    private Long listaPrecioId;
    private String listaPrecioDenominacion;
    private String observaciones;
    private String estado;
    private String cae;
    private Date caeFechaVencimiento;
    private String resultadoAfip;
    private String motivoRechazo;
    private List<FacturaDetalleRequestDTO> detalles;

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

    // Constructor usado en FacturaRepositoryImpl para listado con 8 argumentos
    public FacturaReporteDTO(Long id, Long numeroFactura, Date fechaEmision, String clienteDenominacion,
                             String condicionIva, String puntoVentaDescripcion,
                             Double importeFinal, Long cantidadItems) {
        this.id = id;
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.clienteDenominacion = clienteDenominacion;
        this.condicionIva = condicionIva;
        this.puntoVentaDescripcion = puntoVentaDescripcion;
        this.importeFinal = importeFinal;
        this.cantidadItems = cantidadItems;
    }

    // Constructor con datos AFIP (CAE y vencimiento) para listados paginados
    public FacturaReporteDTO(Long id, Long numeroFactura, Date fechaEmision, String clienteDenominacion,
                             String condicionIva, String puntoVentaDescripcion,
                             Double importeFinal, Long cantidadItems,
                             String cae, Date caeFechaVencimiento, String resultadoAfip) {
        this(id, numeroFactura, fechaEmision, clienteDenominacion, condicionIva, puntoVentaDescripcion, importeFinal, cantidadItems);
        this.cae = cae;
        this.caeFechaVencimiento = caeFechaVencimiento;
        this.resultadoAfip = resultadoAfip;
    }
}
