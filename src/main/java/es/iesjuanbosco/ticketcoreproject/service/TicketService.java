package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.dto.TicketDTO;
import es.iesjuanbosco.ticketcoreproject.exception.SoldOutException;
import es.iesjuanbosco.ticketcoreproject.mapper.TicketMapper;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import es.iesjuanbosco.ticketcoreproject.model.Ticket;
import es.iesjuanbosco.ticketcoreproject.model.Usuario;
import es.iesjuanbosco.ticketcoreproject.repository.EventoRepo;
import es.iesjuanbosco.ticketcoreproject.repository.TicketRepo;
import es.iesjuanbosco.ticketcoreproject.repository.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TicketService {

    @Autowired
    private TicketRepo ticketRepository;

    @Autowired
    private EventoRepo eventoRepository;

    @Autowired
    private UsuarioRepo usuarioRepository;

    @Autowired
    private TicketMapper ticketMapper; // Inyección de MapStruct

    @Transactional
    public TicketDTO comprarTicket(Long usuarioId, Long eventoId) {

        // 1. Recuperar entidades
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // 2. Validar aforo
        long entradasVendidas = ticketRepository.countByEventoId(eventoId);
        int aforoMaximo = evento.getRecinto().getAforoMaximo();

        if (entradasVendidas >= aforoMaximo) {
            throw new SoldOutException("Lo sentimos, no quedan entradas para el evento: " + evento.getTitulo());
        }

        // 3. Crear Ticket
        Ticket ticket = new Ticket();
        ticket.setUsuario(usuario);
        ticket.setEvento(evento);
        ticket.setFechaCompra(LocalDateTime.now());
        ticket.setCodigo(UUID.randomUUID().toString());

        // 4. Guardar
        Ticket ticketGuardado = ticketRepository.save(ticket);

        // 5. Convertir con MapStruct
        return ticketMapper.toDTO(ticketGuardado);
    }
}