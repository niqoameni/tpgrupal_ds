package com.utn.frm.desarrollosoftware;

import com.utn.frm.desarrollosoftware.afip.AfipWsaaClient;
import com.utn.frm.desarrollosoftware.afip.AfipWsfeClient;

/**
 * Clase de prueba directa del flujo completo ARCA / AFIP Homologación (WSAA + WSFEv1),
 * según el Paso 3 de la Guía Técnica Integral.
 * Utiliza las credenciales incluidas dentro del repositorio en src/main/resources/afip/.
 */
public class Main {

    public static void main(String[] args) {
        try {
            String keyPath = "classpath:afip/privada_test.key";
            String certPath = "classpath:afip/certificado_test.crt";
            long cuit = 20446629698L; // CUIT del certificado en Homologación
            int puntoDeVenta = 1;
            int cbteTipo = 11; // 11 = Factura C

            System.out.println("1. Solicitando Token y Sign a WSAA...");
            String[] auth = AfipWsaaClient.obtenerTokenYSign(keyPath, certPath, "wsfe");
            String token = auth[0];
            String sign = auth[1];
            System.out.println("Autenticado OK. Token activo.");

            System.out.println("2. Consultando último comprobante autorizado en WSFEv1...");
            int ultimoNro = AfipWsfeClient.obtenerUltimoComprobante(token, sign, cuit, puntoDeVenta, cbteTipo);
            int nuevoNro = ultimoNro + 1;
            System.out.println("Último número registrado: " + ultimoNro + ". Siguiente a emitir: " + nuevoNro);

            System.out.println("3. Enviando factura para CAE...");
            String respuestaCae = AfipWsfeClient.solicitarCae(token, sign, cuit, puntoDeVenta, nuevoNro, 1500.00);

            System.out.println("\n--- Respuesta WSFEv1 ---");
            System.out.println(respuestaCae);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
