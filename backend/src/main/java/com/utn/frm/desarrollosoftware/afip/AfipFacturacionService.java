package com.utn.frm.desarrollosoftware.afip;

import com.utn.frm.desarrollosoftware.entities.FacturaVenta;
import com.utn.frm.desarrollosoftware.entities.FacturaVentaDetalle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AfipFacturacionService {

    @Value("${afip.enabled:true}")
    private boolean enabled;

    @Value("${afip.cuit:20446629698}")
    private long cuitEmisor;

    @Value("${afip.key-path:classpath:afip/privada_test.key}")
    private String keyPath;

    @Value("${afip.cert-path:classpath:afip/certificado_test.crt}")
    private String certPath;

    @Value("${afip.punto-venta:1}")
    private int puntoVentaDefault;

    @Value("${afip.cbte-tipo:11}")
    private int cbteTipoDefault;

    /**
     * Solicita el CAE en el entorno de Homologación de ARCA/AFIP para la factura indicada
     * y asigna los campos cae, caeFechaVencimiento, resultadoAfip y motivoRechazo.
     */
    public AfipCaeResponse autorizarFactura(FacturaVenta factura) {
        if (!enabled) {
            log.info("Integración ARCA/AFIP deshabilitada por configuración (afip.enabled=false).");
            return null;
        }

        try {
            log.info("Solicitando Token y Sign a WSAA (CUIT emisor: {})...", cuitEmisor);
            String[] auth = AfipWsaaClient.obtenerTokenYSign(keyPath, certPath, "wsfe");
            String token = auth[0];
            String sign = auth[1];

            int ptoVta = puntoVentaDefault;
            int cbteTipo = cbteTipoDefault;

            int ultimoNro = AfipWsfeClient.obtenerUltimoComprobante(token, sign, cuitEmisor, ptoVta, cbteTipo);
            long nuevoNroAfip = ultimoNro + 1L;
            log.info("Último comprobante en ARCA (PtoVta={}, Tipo={}): {}. Siguiente: {}",
                    ptoVta, cbteTipo, ultimoNro, nuevoNroAfip);

            // En AFIP/ARCA, si el importe supera el tope de Consumidor Final sin identificar,
            // exige informar un documento (ej. DocTipo=96 DNI). Con DocTipo=96 y un DNI numérico,
            // ARCA autoriza facturas de cualquier monto en Homologación sin validar padrón de CUIT.
            int docTipo = 99;
            long docNro = 0L;
            int condicionIvaReceptor = 5;

            double impTotal = factura.getImporteTotal() > 0 ? factura.getImporteTotal() : 100.0;
            if (impTotal >= 100000.0) {
                docTipo = 96; // DNI
                docNro = 44662969L;
            }

            double impNeto = 0.0;
            double impIva = 0.0;
            if (factura.getDetalles() != null) {
                for (FacturaVentaDetalle det : factura.getDetalles()) {
                    impNeto += det.getImporteNeto();
                    impIva += det.getImporteIva();
                }
            }

            AfipCaeResponse resp = AfipWsfeClient.solicitarCaeDetallado(
                    token,
                    sign,
                    cuitEmisor,
                    ptoVta,
                    cbteTipo,
                    nuevoNroAfip,
                    docTipo,
                    docNro,
                    condicionIvaReceptor,
                    impTotal,
                    impNeto,
                    impIva
            );

            factura.setResultadoAfip(resp.getResultado());
            if (resp.isAprobado()) {
                factura.setCae(resp.getCae());
                factura.setCaeFechaVencimiento(resp.getCaeFechaVencimiento());
                factura.setMotivoRechazo(resp.getObservaciones());
                log.info("Factura autorizada en ARCA/AFIP -> CAE: {}, Vto: {}, Nro AFIP: {}",
                        resp.getCae(), resp.getCaeFechaVencimientoStr(), nuevoNroAfip);
            } else {
                factura.setMotivoRechazo(resp.getObservaciones());
                log.warn("Factura rechazada u observada por ARCA/AFIP -> Resultado: {}, Motivo: {}",
                        resp.getResultado(), resp.getObservaciones());
            }
            return resp;

        } catch (Exception e) {
            log.warn("No se pudo obtener CAE en ARCA/AFIP: {}", e.getMessage());
            factura.setResultadoAfip("E");
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            if (msg.length() > 250) {
                msg = msg.substring(0, 250);
            }
            factura.setMotivoRechazo(msg);
            return null;
        }
    }
}
