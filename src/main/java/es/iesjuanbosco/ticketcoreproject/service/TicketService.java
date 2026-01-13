package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.dto.TicketDTO;
import es.iesjuanbosco.ticketcoreproject.exception.SoldOutException;
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

    @Transactional
    public TicketDTO comprarTicket(Long usuarioId, Long eventoId) {

        // 1. Recuperar las entidades (o lanzar error si no existen)
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Contamos cuántos tickets se han vendido para este evento
        long entradasVendidas = ticketRepository.countByEventoId(eventoId);
        int aforoMaximo = evento.getRecinto().getAforoMaximo();

        if (entradasVendidas >= aforoMaximo) {
            throw new SoldOutException("Lo sentimos, no quedan entradas para el evento: " + evento.getTitulo());
        }

        // 3. Crear el Ticket (Entidad Transaccional)
        Ticket ticket = new Ticket();
        ticket.setUsuario(usuario);
        ticket.setEvento(evento);
        ticket.setFechaCompra(LocalDateTime.now());
        ticket.setCodigo(UUID.randomUUID().toString()); // Aleatorio

        // 4. Guardar en Base de Datos
        Ticket ticketGuardado = ticketRepository.save(ticket);

        // 5. Convertir a DTO para devolver al controlador
        return convertirADTO(ticketGuardado);
    }

    // Mapper de Entidad a DTO
    private TicketDTO convertirADTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setCodigo(ticket.getCodigo());
        dto.setFechaCompra(ticket.getFechaCompra());
        dto.setTituloEvento(ticket.getEvento().getTitulo());
        dto.setNombreUsuario(ticket.getUsuario().getNombre());
        return dto;
    }
}