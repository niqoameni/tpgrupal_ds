package com.utn.frm.desarrollosoftware.afip;

import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Cliente de Autenticación WSAA (ARCA / AFIP Homologación).
 * Genera y firma el Ticket de Requerimiento de Acceso (TRA) mediante CMS/PKCS#7
 * con BouncyCastle y obtiene el par {token, sign}. Incluye caché local del TA
 * para evitar el error "El CEE ya posee un TA valido" en llamadas consecutivas.
 */
public class AfipWsaaClient {

    public static final String URL_WSAA_HOMO = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";

    public static String[] obtenerTokenYSign(String keyPath, String certPath) throws Exception {
        return obtenerTokenYSign(keyPath, certPath, "wsfe");
    }

    public static String[] obtenerTokenYSign(String keyPath, String certPath, String service) throws Exception {
        asegurarProveedorBC();
        PrivateKey privateKey = cargarClavePrivada(keyPath);
        X509Certificate cert = cargarCertificado(certPath);
        return autenticarConCache(privateKey, cert, service);
    }

    public static String[] obtenerTokenYSignPfx(String pfxPath, String pfxPassword, String service) throws Exception {
        asegurarProveedorBC();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream fis = abrirRecursoOArchivo(pfxPath, "/afip/certificado.pfx")) {
            ks.load(fis, pfxPassword != null ? pfxPassword.toCharArray() : new char[0]);
        }
        Enumeration<String> aliases = ks.aliases();
        if (!aliases.hasMoreElements()) {
            throw new IllegalArgumentException("El archivo PFX no contiene ningún alias/certificado: " + pfxPath);
        }
        String alias = aliases.nextElement();
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, pfxPassword != null ? pfxPassword.toCharArray() : new char[0]);
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
        return autenticarConCache(privateKey, cert, service);
    }

    private static void asegurarProveedorBC() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private static String[] autenticarConCache(PrivateKey privateKey, X509Certificate cert, String service) throws Exception {
        String cacheKey = cert.getSerialNumber().toString(16) + "_" + service;
        Path cacheFile = Path.of(System.getProperty("java.io.tmpdir"), "afip_ta_" + cacheKey + ".properties");

        if (Files.exists(cacheFile)) {
            try (InputStream is = Files.newInputStream(cacheFile)) {
                Properties props = new Properties();
                props.load(is);
                String token = props.getProperty("token");
                String sign = props.getProperty("sign");
                String expStr = props.getProperty("expirationTime");
                if (token != null && sign != null && expStr != null) {
                    ZonedDateTime exp = ZonedDateTime.parse(expStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    if (ZonedDateTime.now().isBefore(exp.minusMinutes(2))) {
                        return new String[]{token, sign};
                    }
                }
            } catch (Exception ignored) {
                // Si el archivo de caché está corrupto o vencido, solicitamos un nuevo TA.
            }
        }

        ZonedDateTime now = ZonedDateTime.now();
        String genTime = now.minusMinutes(5).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String expTime = now.plusMinutes(600).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String traXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<loginTicketRequest version=\"1.0\">" +
                "<header>" +
                "<uniqueId>" + (System.currentTimeMillis() / 1000) + "</uniqueId>" +
                "<generationTime>" + genTime + "</generationTime>" +
                "<expirationTime>" + expTime + "</expirationTime>" +
                "</header>" +
                "<service>" + service + "</service>" +
                "</loginTicketRequest>";

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner sha256Signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(privateKey);

        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                .build(sha256Signer, cert));

        gen.addCertificates(new JcaCertStore(Collections.singletonList(cert)));
        CMSSignedData signedData = gen.generate(
                new CMSProcessableByteArray(traXml.getBytes(StandardCharsets.UTF_8)),
                true
        );

        String cmsBase64 = Base64.getEncoder().encodeToString(signedData.getEncoded());

        String soapEnvelope = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:nso=\"http://wsaa.afip.gov.ar/ws/services/LoginCms\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<nso:loginCms><nso:in0>" + cmsBase64 + "</nso:in0></nso:loginCms>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        String xmlResponse = enviarSoap(URL_WSAA_HOMO, soapEnvelope);
        String xmlLimpio = xmlResponse
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"");

        if (!xmlLimpio.contains("<token>") || !xmlLimpio.contains("<sign>")) {
            String fault = extraerTag(xmlLimpio, "faultstring");
            throw new IllegalStateException("Error de autenticación en WSAA ARCA/AFIP: "
                    + (fault != null ? fault : xmlLimpio));
        }

        String token = extraerTag(xmlLimpio, "token");
        String sign = extraerTag(xmlLimpio, "sign");
        String expResp = extraerTag(xmlLimpio, "expirationTime");

        try (OutputStream os = Files.newOutputStream(cacheFile)) {
            Properties props = new Properties();
            props.setProperty("token", token);
            props.setProperty("sign", sign);
            props.setProperty("expirationTime", expResp != null ? expResp : expTime);
            props.store(os, "ARCA/AFIP WSAA TA Cache");
        } catch (Exception ignored) {
        }

        return new String[]{token, sign};
    }

    private static PrivateKey cargarClavePrivada(String path) throws Exception {
        try (InputStream is = abrirRecursoOArchivo(path, "/afip/privada_test.key")) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            content = content
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] keyBytes = Base64.getDecoder().decode(content);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        }
    }

    private static X509Certificate cargarCertificado(String path) throws Exception {
        try (InputStream is = abrirRecursoOArchivo(path, "/afip/certificado_test.crt")) {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
        }
    }

    /**
     * Busca el archivo en ruta directa, en ruta relativa dentro de backend/, o en el classpath (/afip/...).
     */
    private static InputStream abrirRecursoOArchivo(String path, String defaultClasspathResource) throws IOException {
        if (path != null && !path.isBlank()) {
            String clean = path.trim();
            if (clean.startsWith("classpath:")) {
                String resPath = clean.substring("classpath:".length()).trim();
                String withSlash = resPath.startsWith("/") ? resPath : "/" + resPath;
                String withoutSlash = resPath.startsWith("/") ? resPath.substring(1) : resPath;

                InputStream cpStream = AfipWsaaClient.class.getResourceAsStream(withSlash);
                if (cpStream != null) {
                    return cpStream;
                }
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    cpStream = cl.getResourceAsStream(withoutSlash);
                    if (cpStream != null) {
                        return cpStream;
                    }
                }
                File fRes1 = new File("src/main/resources", withoutSlash);
                if (fRes1.exists() && fRes1.isFile()) {
                    return new FileInputStream(fRes1);
                }
                File fRes2 = new File("backend/src/main/resources", withoutSlash);
                if (fRes2.exists() && fRes2.isFile()) {
                    return new FileInputStream(fRes2);
                }
            } else {
                File f1 = new File(clean);
                if (f1.exists() && f1.isFile()) {
                    return new FileInputStream(f1);
                }
                File f2 = new File("backend", clean);
                if (f2.exists() && f2.isFile()) {
                    return new FileInputStream(f2);
                }
            }
        }
        InputStream fallback = AfipWsaaClient.class.getResourceAsStream(defaultClasspathResource);
        if (fallback != null) {
            return fallback;
        }
        String cleanDefault = defaultClasspathResource.startsWith("/")
                ? defaultClasspathResource.substring(1)
                : defaultClasspathResource;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            fallback = cl.getResourceAsStream(cleanDefault);
            if (fallback != null) {
                return fallback;
            }
        }
        File fallbackFile1 = new File("src/main/resources", cleanDefault);
        if (fallbackFile1.exists() && fallbackFile1.isFile()) {
            return new FileInputStream(fallbackFile1);
        }
        File fallbackFile2 = new File("backend/src/main/resources", cleanDefault);
        if (fallbackFile2.exists() && fallbackFile2.isFile()) {
            return new FileInputStream(fallbackFile2);
        }
        throw new FileNotFoundException("No se encontró el archivo o recurso ARCA/AFIP: " + path + " (fallback: " + defaultClasspathResource + ")");
    }

    private static String enviarSoap(String urlStr, String soapXml) throws Exception {
        URL url = URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(20000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"\"");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapXml.getBytes(StandardCharsets.UTF_8));
        }
        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
        if (is == null) {
            throw new IOException("WSAA devolvió código HTTP " + status + " sin cuerpo de respuesta.");
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
}
