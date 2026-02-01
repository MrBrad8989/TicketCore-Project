package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.model.Compra;
import es.iesjuanbosco.ticketcoreproject.model.Ticket;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    public byte[] generarPdfCompra(Compra compra) {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<Ticket> tickets = compra.getTickets();
            if (tickets == null || tickets.isEmpty()) {
                // crear una página simple con resumen de la compra
                PDPage page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    cs.newLineAtOffset(50, 750);
                    cs.showText("Compra ID: " + (compra.getId() != null ? compra.getId() : "(sin id)"));
                    cs.newLineAtOffset(0, -24);
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.showText("Total: " + (compra.getTotal() != null ? compra.getTotal().toString() : "0"));
                    cs.endText();
                }
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                DateTimeFormatter dfShort = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                for (Ticket t : tickets) {
                    PDPage page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                        float x = 50;
                        float y = 750;

                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA_BOLD, 20);
                        cs.newLineAtOffset(x, y);
                        cs.showText("TICKET");
                        cs.endText();

                        y -= 36;

                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
                        cs.newLineAtOffset(x, y);
                        cs.showText("Código: ");
                        cs.setFont(PDType1Font.COURIER_BOLD, 12);
                        cs.showText(t.getCodigo() != null ? t.getCodigo() : "-");
                        cs.endText();

                        y -= 24;

                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA, 12);
                        cs.newLineAtOffset(x, y);
                        String evento = t.getEvento() != null ? t.getEvento().getTitulo() : "-";
                        cs.showText("Evento: " + evento);
                        cs.endText();

                        y -= 18;

                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA, 12);
                        cs.newLineAtOffset(x, y);
                        String fecha = t.getEvento() != null && t.getEvento().getFechaEvento() != null ? t.getEvento().getFechaEvento().format(dtf) : "-";
                        cs.showText("Fecha evento: " + fecha);
                        cs.endText();

                        y -= 18;

                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA, 12);
                        cs.newLineAtOffset(x, y);
                        String lugar = t.getEvento() != null && t.getEvento().getRecinto() != null ? t.getEvento().getRecinto().getNombre() : "-";
                        cs.showText("Lugar: " + lugar);
                        cs.endText();

                        y -= 24;

                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        cs.newLineAtOffset(x, y);
                        cs.showText("Titular:");
                        cs.endText();

                        y -= 18;

                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA, 12);
                        cs.newLineAtOffset(x, y);
                        String nombre = t.getCompradorNombre() != null ? t.getCompradorNombre() : (t.getUsuario() != null ? t.getUsuario().getNombre() : "-");
                        String email = t.getCompradorEmail() != null ? t.getCompradorEmail() : (t.getUsuario() != null ? t.getUsuario().getEmail() : "-");
                        cs.showText(nombre + " (" + email + ")");
                        cs.endText();

                        // Si hay documento o fecha de nacimiento, añadirlos debajo
                        y -= 18;
                        boolean addedExtra = false;
                        if (t.getCompradorDocumento() != null && !t.getCompradorDocumento().isBlank()) {
                            cs.beginText();
                            cs.setFont(PDType1Font.HELVETICA, 12);
                            cs.newLineAtOffset(x, y);
                            cs.showText("Documento: " + t.getCompradorDocumento());
                            cs.endText();
                            y -= 16;
                            addedExtra = true;
                        }
                        if (t.getCompradorFechaNacimiento() != null) {
                            cs.beginText();
                            cs.setFont(PDType1Font.HELVETICA, 12);
                            cs.newLineAtOffset(x, y);
                            cs.showText("Fecha Nac.: " + t.getCompradorFechaNacimiento().format(dfShort));
                            cs.endText();
                            y -= 16;
                            addedExtra = true;
                        }

                        // Footer: referencia compra
                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                        cs.newLineAtOffset(50, 40);
                        cs.showText("Referencia compra: " + (t.getCompra() != null && t.getCompra().getReferenciaPago() != null ? t.getCompra().getReferenciaPago() : "-"));
                        cs.endText();

                    }
                }
            }

            doc.save(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    public byte[] generarPdfTicket(Ticket t) {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float x = 50;
                float y = 750;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 20);
                cs.newLineAtOffset(x, y);
                cs.showText("TICKET");
                cs.endText();

                y -= 36;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
                cs.newLineAtOffset(x, y);
                cs.showText("Código: ");
                cs.setFont(PDType1Font.COURIER_BOLD, 12);
                cs.showText(t.getCodigo() != null ? t.getCodigo() : "-");
                cs.endText();

                y -= 24;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(x, y);
                String evento = t.getEvento() != null ? t.getEvento().getTitulo() : "-";
                cs.showText("Evento: " + evento);
                cs.endText();

                y -= 18;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(x, y);
                String fecha = t.getEvento() != null && t.getEvento().getFechaEvento() != null ? t.getEvento().getFechaEvento().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "-";
                cs.showText("Fecha evento: " + fecha);
                cs.endText();

                y -= 18;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(x, y);
                String lugar = t.getEvento() != null && t.getEvento().getRecinto() != null ? t.getEvento().getRecinto().getNombre() : "-";
                cs.showText("Lugar: " + lugar);
                cs.endText();

                y -= 24;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cs.newLineAtOffset(x, y);
                cs.showText("Titular:");
                cs.endText();

                y -= 18;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(x, y);
                String nombre = t.getCompradorNombre() != null ? t.getCompradorNombre() : (t.getUsuario() != null ? t.getUsuario().getNombre() : "-");
                String email = t.getCompradorEmail() != null ? t.getCompradorEmail() : (t.getUsuario() != null ? t.getUsuario().getEmail() : "-");
                cs.showText(nombre + " (" + email + ")");
                cs.endText();

                // Añadir documento y fecha si existen
                y -= 18;
                DateTimeFormatter dfShort = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (t.getCompradorDocumento() != null && !t.getCompradorDocumento().isBlank()) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(x, y);
                    cs.showText("Documento: " + t.getCompradorDocumento());
                    cs.endText();
                    y -= 16;
                }
                if (t.getCompradorFechaNacimiento() != null) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(x, y);
                    cs.showText("Fecha Nac.: " + t.getCompradorFechaNacimiento().format(dfShort));
                    cs.endText();
                    y -= 16;
                }

                // Footer: referencia compra
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                cs.newLineAtOffset(50, 40);
                cs.showText("Referencia compra: " + (t.getCompra() != null && t.getCompra().getReferenciaPago() != null ? t.getCompra().getReferenciaPago() : "-"));
                cs.endText();
            }

            doc.save(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF del ticket: " + e.getMessage(), e);
        }
    }
}
