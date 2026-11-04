package com.utn.frm.desarrollosoftware.afip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Cliente SOAP para el Web Service de Facturación Electrónica WSFEv1 de ARCA / AFIP (Homologación).
 * Implementa FECompUltimoAutorizado y FECAESolicitar.
 */
public class AfipWsfeClient {

    public static final String URL_WSFE_HOMO = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";
    private static final String NS_AR = "http://ar.gov.afip.dif.FEV1/";

    /**
     * Consulta el último número de comprobante autorizado en ARCA/AFIP para el punto de venta y tipo de comprobante.
     */
    public static int obtenerUltimoComprobante(String token, String sign, long cuit, int puntoDeVenta, int cbteTipo) throws Exception {
        String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<FECompUltimoAutorizado xmlns=\"" + NS_AR + "\">" +
                "<Auth>" +
                "<Token>" + escaparXml(token) + "</Token>" +
                "<Sign>" + escaparXml(sign) + "</Sign>" +
                "<Cuit>" + cuit + "</Cuit>" +
                "</Auth>" +
                "<PtoVta>" + puntoDeVenta + "</PtoVta>" +
                "<CbteTipo>" + cbteTipo + "</CbteTipo>" +
                "</FECompUltimoAutorizado>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        String xmlResp = enviarSoap(URL_WSFE_HOMO, NS_AR + "FECompUltimoAutorizado", soap);

        String errMsg = extraerMensajesTag(xmlResp, "Err");
        if (errMsg != null && !errMsg.isBlank()) {
            throw new IllegalStateException("Error WSFEv1 en FECompUltimoAutorizado: " + errMsg);
        }

        String cbteNroStr = extraerTag(xmlResp, "CbteNro");
        if (cbteNroStr == null) {
            throw new IllegalStateException("Respuesta inesperada de FECompUltimoAutorizado: " + xmlResp);
        }
        return Integer.parseInt(cbteNroStr);
    }

    /**
     * Método compatible con la clase Main de la guía (página 10 del PDF):
     * emite una Factura C (cbteTipo = 11) o Factura B (cbteTipo = 6) y devuelve el resumen + XML.
     */
    public static String solicitarCae(String token, String sign, long cuit, int puntoDeVenta, int nuevoNro, double importeTotal) throws Exception {
        AfipCaeResponse resp = solicitarCaeDetallado(
                token,
                sign,
                cuit,
                puntoDeVenta,
                11, // Factura C por defecto para pruebas de homologación
                nuevoNro,
                99, // 99 = Consumidor Final
                0L,
                5,  // Condición IVA Receptor: 5 = Consumidor Final
                importeTotal,
                importeTotal,
                0.0
        );
        if (resp.isAprobado()) {
            return "¡Factura autorizada con éxito! Resultado=" + resp.getResultado()
                    + " | Comprobante N°=" + resp.getNumeroComprobante()
                    + " | CAE=" + resp.getCae()
                    + " | Vencimiento=" + resp.getCaeFechaVencimientoStr()
                    + "\nXML: " + resp.getRawXml();
        }
        return "Rechazado por ARCA/AFIP (Resultado=" + resp.getResultado() + "): "
                + resp.getObservaciones()
                + "\nXML: " + resp.getRawXml();
    }

    /**
     * Invoca FECAESolicitar y devuelve un objeto AfipCaeResponse con CAE, vencimiento, resultado y observaciones.
     */
    public static AfipCaeResponse solicitarCaeDetallado(
            String token,
            String sign,
            long cuit,
            int puntoDeVenta,
            int cbteTipo,
            long nuevoNro,
            int docTipo,
            long docNro,
            int condicionIvaReceptorId,
            double impTotal,
            double impNeto,
            double impIva
    ) throws Exception {
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Si es Factura C (11), AFIP exige que ImpIVA sea 0 y que ImpNeto == ImpTotal
        boolean esFacturaC = (cbteTipo == 11);
        double totalRedondeado = redondear2(impTotal);
        double netoRedondeado = esFacturaC ? totalRedondeado : redondear2(impNeto);
        double ivaRedondeado = esFacturaC ? 0.00 : redondear2(impIva);
        if (!esFacturaC) {
            totalRedondeado = redondear2(netoRedondeado + ivaRedondeado);
        }

        StringBuilder ivaXml = new StringBuilder();
        if (!esFacturaC && ivaRedondeado > 0) {
            ivaXml.append("<Iva><AlicIva>")
                    .append("<Id>5</Id>") // 5 = 21%
                    .append("<BaseImp>").append(formatearMonto(netoRedondeado)).append("</BaseImp>")
                    .append("<Importe>").append(formatearMonto(ivaRedondeado)).append("</Importe>")
                    .append("</AlicIva></Iva>");
        }

        String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<FECAESolicitar xmlns=\"" + NS_AR + "\">" +
                "<Auth>" +
                "<Token>" + escaparXml(token) + "</Token>" +
                "<Sign>" + escaparXml(sign) + "</Sign>" +
                "<Cuit>" + cuit + "</Cuit>" +
                "</Auth>" +
                "<FeCAEReq>" +
                "<FeCabReq>" +
                "<CantReg>1</CantReg>" +
                "<PtoVta>" + puntoDeVenta + "</PtoVta>" +
                "<CbteTipo>" + cbteTipo + "</CbteTipo>" +
                "</FeCabReq>" +
                "<FeDetReq>" +
                "<FECAEDetRequest>" +
                "<Concepto>1</Concepto>" +
                "<DocTipo>" + docTipo + "</DocTipo>" +
                "<DocNro>" + docNro + "</DocNro>" +
                "<CbteDesde>" + nuevoNro + "</CbteDesde>" +
                "<CbteHasta>" + nuevoNro + "</CbteHasta>" +
                "<CbteFch>" + fechaHoy + "</CbteFch>" +
                "<ImpTotal>" + formatearMonto(totalRedondeado) + "</ImpTotal>" +
                "<ImpTotConc>0.00</ImpTotConc>" +
                "<ImpNeto>" + formatearMonto(netoRedondeado) + "</ImpNeto>" +
                "<ImpOpEx>0.00</ImpOpEx>" +
                "<ImpTrib>0.00</ImpTrib>" +
                "<ImpIVA>" + formatearMonto(ivaRedondeado) + "</ImpIVA>" +
                "<MonId>PES</MonId>" +
                "<MonCotiz>1</MonCotiz>" +
                "<CondicionIVAReceptorId>" + condicionIvaReceptorId + "</CondicionIVAReceptorId>" +
                ivaXml +
                "</FECAEDetRequest>" +
                "</FeDetReq>" +
                "</FeCAEReq>" +
                "</FECAESolicitar>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        String xmlResp = enviarSoap(URL_WSFE_HOMO, NS_AR + "FECAESolicitar", soap);

        String resultado = extraerTag(xmlResp, "Resultado");
        String cae = extraerTag(xmlResp, "CAE");
        String caeFchVto = extraerTag(xmlResp, "CAEFchVto");
        String obs = extraerMensajesTag(xmlResp, "Obs");
        String errs = extraerMensajesTag(xmlResp, "Err");

        StringBuilder motivo = new StringBuilder();
        if (obs != null && !obs.isBlank()) motivo.append(obs);
        if (errs != null && !errs.isBlank()) {
            if (motivo.length() > 0) motivo.append(" | ");
            motivo.append(errs);
        }

        Date fechaVtoDate = null;
        if (caeFchVto != null && caeFchVto.length() == 8) {
            try {
                fechaVtoDate = new SimpleDateFormat("yyyyMMdd").parse(caeFchVto);
            } catch (Exception ignored) {
            }
        }

        return new AfipCaeResponse(
                resultado != null ? resultado : "R",
                cae,
                caeFchVto,
                fechaVtoDate,
                nuevoNro,
                puntoDeVenta,
                cbteTipo,
                motivo.length() > 0 ? motivo.toString() : null,
                xmlResp
        );
    }

    private static String enviarSoap(String urlStr, String soapAction, String soapXml) throws Exception {
        URL url = URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(20000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"" + soapAction + "\"");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapXml.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
        if (is == null) {
            throw new IOException("WSFEv1 devolvió código HTTP " + status + " sin cuerpo de respuesta.");
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private static String formatearMonto(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private static double redondear2(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private static String escaparXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String extraerTag(String xml, String tag) {
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int i = xml.indexOf(open);
        int j = xml.indexOf(close);
        if (i >= 0 && j > i) {
            return xml.substring(i + open.length(), j).trim();
        }
        return null;
    }

    private static String extraerMensajesTag(String xml, String blockTag) {
        StringBuilder sb = new StringBuilder();
        String open = "<" + blockTag + ">";
        String close = "</" + blockTag + ">";
        int from = 0;
        while (true) {
            int i = xml.indexOf(open, from);
            if (i < 0) break;
            int j = xml.indexOf(close, i);
            if (j < 0) break;
            String bloque = xml.substring(i + open.length(), j);
            String code = extraerTag(bloque, "Code");
            String msg = extraerTag(bloque, "Msg");
            if (sb.length() > 0) sb.append("; ");
            if (code != null) sb.append("[").append(code).append("] ");
            if (msg != null) sb.append(msg);
            from = j + close.length();
        }
        return sb.length() > 0 ? sb.toString() : null;
    }
}

