package es.iesjuanbosco.ticketcoreproject.controller;

import es.iesjuanbosco.ticketcoreproject.dto.TicketDTO;
import es.iesjuanbosco.ticketcoreproject.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // POST http://localhost:8080/api/tickets/comprar?usuarioId=1&eventoId=1
    @PostMapping("/comprar")
    public ResponseEntity<TicketDTO> comprarTicket(
            @RequestParam Long usuarioId,
            @RequestParam Long eventoId) {

        // Llamo al servicio que tiene la validación del aforo
        TicketDTO nuevoTicket = ticketService.comprarTicket(usuarioId, eventoId);

        // Devuelvo el JSON del ticket y un código 201 (Created)
        return new ResponseEntity<>(nuevoTicket, HttpStatus.CREATED);
    }
}