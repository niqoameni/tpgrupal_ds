package com.utn.frm.desarrollosoftware.util;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class PdfItextExporter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static void exportar(List<FacturaReporteDTO> lista, String rutaArchivo) {
        try (FileOutputStream fos = new FileOutputStream(rutaArchivo);
             PdfWriter writer = new PdfWriter(fos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, PageSize.A4.rotate())) {

            // Creación de fuentes estándar de iText 9
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Título
            Paragraph titulo = new Paragraph("Reporte de Ventas")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Tabla apaisada de 7 columnas
            float[] anchos = {2f, 2.5f, 4f, 3.5f, 3.5f, 2.5f, 2f};
            Table table = new Table(UnitValue.createPointArray(anchos));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados con fondo azul y texto blanco en negrita
            String[] headers = {"Nro Factura", "Fecha", "Cliente", "Condición IVA", "Pto Venta", "Total ($)", "Items"};
            for (String h : headers) {
                Cell headerCell = new Cell()
                        .add(new Paragraph(h).setFont(boldFont).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(ColorConstants.BLUE)
                        .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(headerCell);
            }

            // Filas de datos del DTO
            for (FacturaReporteDTO dto : lista) {
                String fecha = (dto.getFechaEmision() != null) ? DATE_FORMAT.format(dto.getFechaEmision()) : "";

                table.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getNumeroFactura())).setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(fecha).setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(dto.getClienteDenominacion()).setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(dto.getCondicionIva()).setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(dto.getPuntoVentaDescripcion()).setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", dto.getImporteFinal())).setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getCantidadItems())).setFont(regularFont)));
            }

            document.add(table);
            System.out.println("Archivo PDF generado con éxito en: " + rutaArchivo);

        } catch (Exception e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}