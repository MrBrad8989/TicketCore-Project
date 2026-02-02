package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.dto.EventoDTO;
import es.iesjuanbosco.ticketcoreproject.mapper.EventoMapper;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import es.iesjuanbosco.ticketcoreproject.model.Recinto;
import es.iesjuanbosco.ticketcoreproject.repository.EventoRepo;
import es.iesjuanbosco.ticketcoreproject.repository.ArtistaRepo;
import es.iesjuanbosco.ticketcoreproject.repository.RecintoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepo eventoRepo;

    @Autowired
    private EventoMapper eventoMapper;

    @Autowired
    private ArtistaRepo artistaRepo;

    @Autowired
    private RecintoRepo recintoRepo;

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

        // Persistir o reutilizar Recinto si viene en el DTO
        if (eventoDTO.getRecinto() != null) {
            String ciudad = eventoDTO.getRecinto().getCiudad();
            String nombreRecinto = eventoDTO.getRecinto().getNombre();

            // Buscar si ya existe un recinto con la misma ciudad
            Recinto recintoExistente = recintoRepo.findAll().stream()
                .filter(r -> r.getCiudad().equalsIgnoreCase(ciudad))
                .findFirst()
                .orElse(null);

            if (recintoExistente != null) {
                // Reutilizar recinto existente
                evento.setRecinto(recintoExistente);
            } else {
                // Crear y guardar nuevo recinto
                Recinto nuevoRecinto = new Recinto();
                nuevoRecinto.setCiudad(ciudad);
                nuevoRecinto.setNombre(nombreRecinto != null ? nombreRecinto : "Recinto " + ciudad);
                nuevoRecinto.setAforoMaximo(eventoDTO.getMaxEntradas() != null ? eventoDTO.getMaxEntradas() : 1000);
                evento.setRecinto(recintoRepo.save(nuevoRecinto));
            }
        }

        // Asociar artistas: si vienen nombres en DTO, crear o reutilizar Artistas
        if (eventoDTO.getArtistas() != null && !eventoDTO.getArtistas().isEmpty()) {
            List<es.iesjuanbosco.ticketcoreproject.model.Artista> artistas = new java.util.ArrayList<>();
            for (var aDto : eventoDTO.getArtistas()) {
                String nombre = aDto.getNombre();
                if (nombre == null || nombre.trim().isEmpty()) continue;
                es.iesjuanbosco.ticketcoreproject.model.Artista existing = artistaRepo.findByNombreIgnoreCase(nombre.trim());
                if (existing != null) {
                    artistas.add(existing);
                } else {
                    es.iesjuanbosco.ticketcoreproject.model.Artista nuevo = new es.iesjuanbosco.ticketcoreproject.model.Artista();
                    nuevo.setNombre(nombre.trim());
                    if (aDto.getGenero() != null) nuevo.setGenero(aDto.getGenero());
                    artistas.add(artistaRepo.save(nuevo));
                }
            }
            evento.setArtistas(artistas);
        }

        if (evento.getFechaEvento() == null) evento.setFechaEvento(LocalDateTime.now().plusDays(30));
        if (evento.getPrecio() == null) evento.setPrecio(0.0);
        return eventoMapper.toDTO(eventoRepo.save(evento));
    }

    public EventoDTO actualizarEvento(Long id, EventoDTO eventoDTO) {
        Evento evento = eventoRepo.findById(id).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Solo permitir actualización si el usuario es el creador o admin
        if (eventoDTO.getCreadorId() != null && evento.getCreador() != null) {
            if (!evento.getCreador().getId().equals(eventoDTO.getCreadorId())) {
                throw new RuntimeException("No autorizado: solo el creador puede actualizar este evento");
            }
        }

        if (eventoDTO.getTitulo() != null) evento.setTitulo(eventoDTO.getTitulo());
        if (eventoDTO.getPrecio() != null) evento.setPrecio(eventoDTO.getPrecio());
        if (eventoDTO.getFechaEvento() != null) evento.setFechaEvento(eventoDTO.getFechaEvento());
        if (eventoDTO.getImageUrl() != null) evento.setImageUrl(eventoDTO.getImageUrl());
        if (eventoDTO.getMaxEntradas() != null && evento.getRecinto() != null) {
            evento.getRecinto().setAforoMaximo(eventoDTO.getMaxEntradas());
        }

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

    public List<EventoDTO> obtenerEventosPorCreador(Long creadorId) {
        List<Evento> eventos = eventoRepo.findAll().stream()
                .filter(e -> e.getCreador() != null && e.getCreador().getId().equals(creadorId))
                .collect(java.util.stream.Collectors.toList());
        return eventos.stream().map(eventoMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }
}