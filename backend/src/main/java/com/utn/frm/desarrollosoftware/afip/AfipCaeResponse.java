package com.utn.frm.desarrollosoftware.afip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AfipCaeResponse {
    private String resultado;          // "A" = Aprobado, "R" = Rechazado, "P" = Parcial
    private String cae;                // Código de Autorización Electrónico (14 dígitos)
    private String caeFechaVencimientoStr; // Formato YYYYMMDD devuelto por AFIP
    private Date caeFechaVencimiento;
    private long numeroComprobante;
    private int puntoVenta;
    private int cbteTipo;
    private String observaciones;
    private String rawXml;

    public boolean isAprobado() {
        return "A".equalsIgnoreCase(resultado) && cae != null && !cae.isBlank();
    }
}

