package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.dto.EventoDTO;
import es.iesjuanbosco.ticketcoreproject.mapper.EventoMapper;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import es.iesjuanbosco.ticketcoreproject.repository.EventoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepo eventoRepo;

    @Autowired
    private EventoMapper eventoMapper;

    public Page<EventoDTO> buscarEventos(String ciudad, String keyword, String genero, LocalDateTime fecha, Pageable pageable) {
        Page<Evento> eventos;

        // LÓGICA: Si no hay ciudad seleccionada (o es vacía), mostramos ALEATORIO
        // para dar sensación de "Descubrimiento" y que no salgan siempre los mismos.
        if (ciudad == null || ciudad.trim().isEmpty()) {
            eventos = eventoRepo.buscarEventosAleatorios(ciudad, fecha, keyword, genero, pageable);
        } else {
            // Si busca en una ciudad concreta, orden normal (por defecto ID o lo que diga el Pageable)
            eventos = eventoRepo.buscarEventos(ciudad, fecha, keyword, genero, pageable);
        }

        return eventos.map(eventoMapper::toDTO);
    }

    // --- RESTO DE MÉTODOS CRUD (Mantén los que ya tenías: crear, actualizar, borrar) ---
    public EventoDTO crearEvento(EventoDTO eventoDTO) {
        Evento evento = eventoMapper.toEntity(eventoDTO);
        if (evento.getFechaEvento() == null) evento.setFechaEvento(LocalDateTime.now().plusDays(30));
        if (evento.getPrecio() == null) evento.setPrecio(0.0);
        return eventoMapper.toDTO(eventoRepo.save(evento));
    }

    public EventoDTO actualizarEvento(Long id, EventoDTO eventoDTO) {
        Evento evento = eventoRepo.findById(id).orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        evento.setTitulo(eventoDTO.getTitulo());
        evento.setPrecio(eventoDTO.getPrecio());
        if(eventoDTO.getFechaEvento() != null) evento.setFechaEvento(eventoDTO.getFechaEvento());
        return eventoMapper.toDTO(eventoRepo.save(evento));
    }

    public void borrarEvento(Long id, Long requesterId) {
        borrarEvento(id, requesterId, null);
    }

    public void borrarEvento(Long id, Long requesterId, String requesterRol) {
        Evento evento = eventoRepo.findById(id).orElseThrow(() -> new RuntimeException("No existe"));

        // Admin puede borrar cualquiera
        if (requesterRol != null && "ADMIN".equalsIgnoreCase(requesterRol)) {
            eventoRepo.deleteById(id);
            return;
        }

        // Si es empresa, solo puede borrar si es el creador
        if (requesterRol == null || "EMPRESA".equalsIgnoreCase(requesterRol)) {
            if (evento.getCreador() != null && evento.getCreador().getId().equals(requesterId)) {
                eventoRepo.deleteById(id);
                return;
            } else {
                throw new RuntimeException("No autorizado: solo la empresa creadora puede borrar este evento");
            }
        }

        // Usuario normal no puede borrar
        throw new RuntimeException("No autorizado");
    }

    public Optional<EventoDTO> obtenerEventoPorId(Long id) {
        return eventoRepo.findById(id).map(eventoMapper::toDTO);
    }
}