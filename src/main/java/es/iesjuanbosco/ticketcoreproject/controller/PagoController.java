package es.iesjuanbosco.ticketcoreproject.controller;

import es.iesjuanbosco.ticketcoreproject.dto.CompraDTO;
import es.iesjuanbosco.ticketcoreproject.mapper.CompraMapper;
import es.iesjuanbosco.ticketcoreproject.model.Compra;
import es.iesjuanbosco.ticketcoreproject.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private CompraMapper compraMapper;

    @PostMapping("/{compraId}/confirm")
    public ResponseEntity<CompraDTO> confirmarPago(@PathVariable Long compraId) {
        Compra compra = compraService.confirmarCompra(compraId);
        CompraDTO dto = compraMapper.toDTO(compra);
        return ResponseEntity.ok(dto);
    }
}
