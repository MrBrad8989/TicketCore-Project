package es.iesjuanbosco.ticketcoreproject.controller;

import es.iesjuanbosco.ticketcoreproject.dto.ArtistaDTO;
import es.iesjuanbosco.ticketcoreproject.dto.EventoDTO;
import es.iesjuanbosco.ticketcoreproject.dto.RecintoDTO;
import es.iesjuanbosco.ticketcoreproject.mapper.EventoMapper;
import es.iesjuanbosco.ticketcoreproject.model.Artista;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import es.iesjuanbosco.ticketcoreproject.model.Recinto;
import es.iesjuanbosco.ticketcoreproject.repository.EventoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoRepo eventoRepository;
    @Autowired
    private EventoMapper eventoMapper;

    // GET http://localhost:8080/api/eventos/buscar?ciudad=Madrid&page=0&size=5
    @GetMapping("/buscar")
    public ResponseEntity<Page<EventoDTO>> buscarEventos(
            @RequestParam String ciudad,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @PageableDefault(size = 9, page = 0) Pageable pageable) {

        // Si no pasan la fecha, uso "Ahora" por defecto para no ver eventos pasados
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now();
        }

        // Ejecuto la consulta avanzada del repositorio
        Page<Evento> resultados = eventoRepository.buscarEventosAvanzado(ciudad, fechaInicio, keyword, pageable);

        // Mapeo la página de entidades a página de DTOs
        Page<EventoDTO> pageDTO = resultados.map(eventoMapper::toDTO);

        return ResponseEntity.ok(pageDTO);
    }
}