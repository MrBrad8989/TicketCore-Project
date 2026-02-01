package es.iesjuanbosco.ticketcoreproject.controller;

import es.iesjuanbosco.ticketcoreproject.dto.CheckoutRequest;
import es.iesjuanbosco.ticketcoreproject.dto.CompradorInfoDTO;
import es.iesjuanbosco.ticketcoreproject.dto.CompraDTO;
import es.iesjuanbosco.ticketcoreproject.dto.CompraDirectaRequest;
import es.iesjuanbosco.ticketcoreproject.mapper.CompraMapper;
import es.iesjuanbosco.ticketcoreproject.model.Compra;
import es.iesjuanbosco.ticketcoreproject.model.Ticket;
import es.iesjuanbosco.ticketcoreproject.repository.CompraRepo;
import es.iesjuanbosco.ticketcoreproject.service.CompraService;
import es.iesjuanbosco.ticketcoreproject.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private CompraMapper compraMapper;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private CompraRepo compraRepo;

    @PostMapping("/checkout")
    public ResponseEntity<CompraDTO> checkout(@RequestBody CheckoutRequest req) {
        Compra compra = compraService.crearCompraDesdeCarrito(req.getUsuarioId(), req.getCompradorInfo());
        CompraDTO dto = compraMapper.toDTO(compra);
        return ResponseEntity.status(201).body(dto);
    }

    @PostMapping("/directo")
    public ResponseEntity<CompraDTO> compraDirecta(@RequestBody CompraDirectaRequest req) {
        Compra compra = compraService.crearCompraDirecta(req);
        CompraDTO dto = compraMapper.toDTO(compra);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Nuevo endpoint para obtener una compra por id (incluye tickets mediante el mapper)
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<CompraDTO> getCompraById(@PathVariable Long id) {
        Compra compra = compraRepo.findById(id).orElseThrow(() -> new RuntimeException("Compra no encontrada: " + id));
        CompraDTO dto = compraMapper.toDTO(compra);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/pdf")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getPdf(@PathVariable Long id) {
        Compra compra = compraRepo.findById(id).orElseThrow(() -> new RuntimeException("Compra no encontrada: " + id));
        byte[] pdf = pdfService.generarPdfCompra(compra);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "compra-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @PostMapping("/carrito/{usuarioId}")
    public ResponseEntity<CompraDTO> checkoutDesdeCarrito(
            @PathVariable Long usuarioId,
            @RequestBody(required = false) CompradorInfoDTO compradorInfo
    ) {
        Compra compra = compraService.crearCompraDesdeCarrito(usuarioId, compradorInfo);
        CompraDTO dto = compraMapper.toDTO(compra);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/zip")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getZipTickets(@PathVariable Long id) {
        Compra compra = compraRepo.findById(id).orElseThrow(() -> new RuntimeException("Compra no encontrada: " + id));
        var tickets = compra.getTickets();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {
            int counter = 1;
            for (Ticket t : tickets) {
                byte[] pdf = pdfService.generarPdfTicket(t);
                ZipEntry entry = new ZipEntry("ticket-" + counter + "-" + t.getId() + ".pdf");
                zos.putNextEntry(entry);
                zos.write(pdf);
                zos.closeEntry();
                counter++;
            }
            zos.finish();
            byte[] zipBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "compra-" + id + "-tickets.zip");
            return ResponseEntity.ok().headers(headers).body(zipBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generando ZIP: " + e.getMessage(), e);
        }
    }
}
