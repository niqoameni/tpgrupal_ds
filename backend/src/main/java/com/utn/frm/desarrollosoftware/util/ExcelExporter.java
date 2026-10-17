package com.utn.frm.desarrollosoftware.util;

import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExcelExporter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static void exportar(List<FacturaReporteDTO> lista, String rutaArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {

            // Encabezados separados por tabulación (\t)
            writer.println("Nro Factura\tFecha Emision\tCliente\tCondicion IVA\tPunto de Venta\tImporte Total\tCant. Items");

            // Filas de datos
            for (FacturaReporteDTO dto : lista) {
                String fecha = (dto.getFechaEmision() != null) ? DATE_FORMAT.format(dto.getFechaEmision()) : "";

                writer.printf("%d\t%s\t%s\t%s\t%s\t%.2f\t%d%n",
                        dto.getNumeroFactura(),
                        fecha,
                        dto.getClienteDenominacion(),
                        dto.getCondicionIva(),
                        dto.getPuntoVentaDescripcion(),
                        dto.getImporteFinal(),
                        dto.getCantidadItems());
            }

            System.out.println("Archivo Excel generado con éxito en: " + rutaArchivo);

        } catch (Exception e) {
            System.err.println("Error al generar el archivo Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}