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
import com.utn.frm.desarrollosoftware.dto.FacturaDetalleRequestDTO;
import com.utn.frm.desarrollosoftware.dto.FacturaReporteDTO;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class PdfItextExporter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static byte[] exportarEnMemoria(List<FacturaReporteDTO> lista) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, PageSize.A4.rotate())) {

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            Paragraph titulo = new Paragraph("Reporte de Ventas")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            float[] anchos = {2f, 2.5f, 4f, 3.5f, 3.5f, 2.5f, 2f};
            Table table = new Table(UnitValue.createPointArray(anchos));
            table.setWidth(UnitValue.createPercentValue(100));

            String[] headers = {"Nro Factura", "Fecha", "Cliente", "Condición IVA", "Pto Venta", "Total ($)", "Items"};
            for (String h : headers) {
                Cell headerCell = new Cell()
                        .add(new Paragraph(h).setFont(boldFont).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(ColorConstants.BLUE)
                        .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(headerCell);
            }

            for (FacturaReporteDTO dto : lista) {
                String fecha = (dto.getFechaEmision() != null) ? DATE_FORMAT.format(dto.getFechaEmision()) : "";

                table.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getNumeroFactura())).setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(fecha).setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(dto.getClienteDenominacion() != null ? dto.getClienteDenominacion() : "").setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(dto.getCondicionIva() != null ? dto.getCondicionIva() : "").setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(dto.getPuntoVentaDescripcion() != null ? dto.getPuntoVentaDescripcion() : "").setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", dto.getImporteFinal() != null ? dto.getImporteFinal() : 0.0)).setFont(regularFont)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getCantidadItems() != null ? dto.getCantidadItems() : 0)).setFont(regularFont)));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF con iText: " + e.getMessage(), e);
        }
    }

    public static byte[] exportarFacturaIndividualEnMemoria(FacturaReporteDTO factura) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, PageSize.A4)) {

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            String nroComprobante = String.valueOf(factura.getNumeroFactura() != null ? factura.getNumeroFactura() : factura.getId());
            String fecha = (factura.getFechaEmision() != null) ? DATE_FORMAT.format(factura.getFechaEmision()) : "-";

            Paragraph titulo = new Paragraph("FACTURA DE VENTA N° " + nroComprobante)
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(12);
            document.add(titulo);

            // Tabla de datos de cabecera de la factura
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50f, 50f}));
            infoTable.setWidth(UnitValue.createPercentValue(100));
            infoTable.setMarginBottom(16);

            String clienteTxt = factura.getClienteDenominacion() != null ? factura.getClienteDenominacion() : "Consumidor Final";
            String cuitTxt = factura.getClienteCuit() != null ? factura.getClienteCuit() : "-";
            String condIvaTxt = factura.getCondicionIva() != null ? factura.getCondicionIva() : "-";
            String ptoVentaTxt = factura.getPuntoVentaDescripcion() != null ? factura.getPuntoVentaDescripcion() : "-";
            String monedaTxt = factura.getTipoMonedaDescripcion() != null ? factura.getTipoMonedaDescripcion() : "Pesos Argentinos";
            String listaTxt = factura.getListaPrecioDenominacion() != null ? factura.getListaPrecioDenominacion() : "Lista General Minorista";

            infoTable.addCell(crearCeldaDatos("Fecha de Emisión: ", fecha, boldFont, regularFont));
            infoTable.addCell(crearCeldaDatos("Punto de Venta: ", ptoVentaTxt, boldFont, regularFont));
            infoTable.addCell(crearCeldaDatos("Cliente: ", clienteTxt, boldFont, regularFont));
            infoTable.addCell(crearCeldaDatos("CUIT / CUIL: ", cuitTxt, boldFont, regularFont));
            infoTable.addCell(crearCeldaDatos("Condición IVA: ", condIvaTxt, boldFont, regularFont));
            infoTable.addCell(crearCeldaDatos("Lista de Precios: ", listaTxt, boldFont, regularFont));
            infoTable.addCell(crearCeldaDatos("Moneda: ", monedaTxt, boldFont, regularFont));
            infoTable.addCell(crearCeldaDatos("Estado: ", factura.getEstado() != null ? factura.getEstado() : "EMITIDA", boldFont, regularFont));

            document.add(infoTable);

            if (factura.getObservaciones() != null && !factura.getObservaciones().trim().isEmpty()) {
                Paragraph obs = new Paragraph("Observaciones: " + factura.getObservaciones())
                        .setFont(regularFont)
                        .setFontSize(10)
                        .setMarginBottom(12);
                document.add(obs);
            }

            Paragraph subtituloDetalles = new Paragraph("Detalle de Artículos")
                    .setFont(boldFont)
                    .setFontSize(13)
                    .setMarginBottom(6);
            document.add(subtituloDetalles);

            float[] anchosDetalle = {3f, 3.5f, 1.5f, 2.2f, 1.5f, 2.5f};
            Table tablaDetalles = new Table(UnitValue.createPointArray(anchosDetalle));
            tablaDetalles.setWidth(UnitValue.createPercentValue(100));

            String[] headersDetalle = {"Artículo", "Descripción", "Cant.", "Precio Unit. ($)", "% Desc.", "Subtotal c/IVA ($)"};
            for (String h : headersDetalle) {
                Cell headerCell = new Cell()
                        .add(new Paragraph(h).setFont(boldFont).setFontSize(10).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(ColorConstants.BLUE)
                        .setTextAlignment(TextAlignment.CENTER);
                tablaDetalles.addHeaderCell(headerCell);
            }

            if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
                for (FacturaDetalleRequestDTO det : factura.getDetalles()) {
                    String artNombre = det.getArticuloDenominacion() != null ? det.getArticuloDenominacion() : "-";
                    String desc = det.getDescripcion() != null ? det.getDescripcion() : artNombre;
                    double cant = det.getCantidad() != null ? det.getCantidad() : 1.0;
                    double precioUnit = det.getPrecioUnitario() != null ? det.getPrecioUnitario() : 0.0;
                    double bonif = det.getPorcentajeBonificacion() != null ? det.getPorcentajeBonificacion() : 0.0;
                    double neto = cant * precioUnit * (1.0 - bonif / 100.0);
                    double subtotalConIva = neto * 1.21;

                    tablaDetalles.addCell(new Cell().add(new Paragraph(artNombre).setFont(regularFont).setFontSize(9.5f)));
                    tablaDetalles.addCell(new Cell().add(new Paragraph(desc).setFont(regularFont).setFontSize(9.5f)));
                    tablaDetalles.addCell(new Cell().add(new Paragraph(String.format("%.0f", cant)).setFont(regularFont).setFontSize(9.5f)).setTextAlignment(TextAlignment.CENTER));
                    tablaDetalles.addCell(new Cell().add(new Paragraph(String.format("$ %.2f", precioUnit)).setFont(regularFont).setFontSize(9.5f)).setTextAlignment(TextAlignment.RIGHT));
                    tablaDetalles.addCell(new Cell().add(new Paragraph(String.format("%.0f%%", bonif)).setFont(regularFont).setFontSize(9.5f)).setTextAlignment(TextAlignment.CENTER));
                    tablaDetalles.addCell(new Cell().add(new Paragraph(String.format("$ %.2f", subtotalConIva)).setFont(boldFont).setFontSize(9.5f)).setTextAlignment(TextAlignment.RIGHT));
                }
            } else {
                Cell emptyCell = new Cell(1, 6)
                        .add(new Paragraph("Sin renglones detallados").setFont(regularFont).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER);
                tablaDetalles.addCell(emptyCell);
            }

            document.add(tablaDetalles);

            double totalFactura = factura.getImporteFinal() != null ? factura.getImporteFinal() : 0.0;
            Paragraph totalParrafo = new Paragraph(String.format("IMPORTE TOTAL (IVA incl.): $ %.2f", totalFactura))
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(16);
            document.add(totalParrafo);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF individual de factura con iText: " + e.getMessage(), e);
        }
    }

    private static Cell crearCeldaDatos(String etiqueta, String valor, PdfFont boldFont, PdfFont regularFont) {
        Paragraph p = new Paragraph()
                .add(new com.itextpdf.layout.element.Text(etiqueta).setFont(boldFont).setFontSize(10))
                .add(new com.itextpdf.layout.element.Text(valor != null ? valor : "-").setFont(regularFont).setFontSize(10));
        return new Cell().add(p).setPadding(5);
    }

    public static void exportar(List<FacturaReporteDTO> lista, String rutaArchivo) {
        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            byte[] bytes = exportarEnMemoria(lista);
            fos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
