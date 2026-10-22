package com.proyecto.safebox.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

import com.proyecto.safebox.model.Mensajes;
import com.proyecto.safebox.model.AuditoriaEvento;
import com.proyecto.safebox.repository.AuditoriaRepository;
import com.proyecto.safebox.repository.MensajesRepository;

@Controller
@RequestMapping("/admin/reportes")
public class AdminReportController {

    private final MensajesRepository mensajesRepo;
    private final AuditoriaRepository auditoriaRepo;

    public AdminReportController(MensajesRepository mensajesRepo, AuditoriaRepository auditoriaRepo) {
        this.mensajesRepo = mensajesRepo;
        this.auditoriaRepo = auditoriaRepo;
    }

    @GetMapping(value = "/panel.pdf")
    public ResponseEntity<byte[]> panelPdf() throws Exception {

        // Ajusta tamaños si quieres
        List<Mensajes> mensajes = mensajesRepo
                .findAll(PageRequest.of(0, 50))
                .getContent();

        List<AuditoriaEvento> auditorias = auditoriaRepo
                .findAll(PageRequest.of(0, 100))
                .getContent();

        byte[] pdfBytes = construirPdf(mensajes, auditorias);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("reporte_panel_admin.pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    private byte[] construirPdf(List<Mensajes> mensajes, List<AuditoriaEvento> auditorias) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document doc = new Document();
        PdfWriter.getInstance(doc, baos);

        doc.open();
        doc.add(new Paragraph("SafeBox - Reporte Panel Admin"));
        doc.add(new Paragraph("Fecha: " + LocalDateTime.now()));
        doc.add(new Paragraph("Total Mensajes: " + mensajesRepo.count()));
        doc.add(new Paragraph("Total Auditorias: " + auditoriaRepo.count()));
        doc.add(new Paragraph(" "));

        doc.add(new Paragraph("Mensajes"));
         doc.add(new Paragraph(" "));
        doc.add(tablaMensajes(mensajes));
        doc.add(new Paragraph(" "));

        doc.add(new Paragraph("Auditorias"));
        doc.add(new Paragraph(" "));
        doc.add(tablaAuditorias(auditorias));

        doc.close();
        return baos.toByteArray();
    }

    private PdfPTable tablaMensajes(List<Mensajes> mensajes) {
        PdfPTable t = new PdfPTable(4);
        t.setWidthPercentage(100);

        t.addCell("ID");
        t.addCell("Titulo");
        t.addCell("Usuario");
        t.addCell("Fecha");

        for (Mensajes m : mensajes) {
            t.addCell(String.valueOf(m.getId()));
            t.addCell(safe(m.getTitulo())); // AJUSTA si tu campo se llama distinto
            t.addCell(m.getUsuario() != null ? safe(m.getUsuario().getNombreCompleto()) : "-"); // AJUSTA username
            t.addCell(m.getFechaCreacion() != null ? m.getFechaCreacion().toString() : "-"); // AJUSTA fecha
        }
        return t;
    }

    private PdfPTable tablaAuditorias(List<AuditoriaEvento> auditorias) {
    PdfPTable t = new PdfPTable(3);
    t.setWidthPercentage(100);

    t.addCell("Fecha");
    t.addCell("Tipo");
    t.addCell("Detalle");

    for (AuditoriaEvento a : auditorias) {
        t.addCell(a.getTimestamp() != null ? a.getTimestamp().toString() : "-");
        t.addCell(safe(a.getTipo()));
        t.addCell(safe(a.getDetalle()));
    }
    return t;
    }
    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
