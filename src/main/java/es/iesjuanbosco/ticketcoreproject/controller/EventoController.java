package es.iesjuanbosco.ticketcoreproject.controller;

import es.iesjuanbosco.ticketcoreproject.dto.EventoDTO;
import es.iesjuanbosco.ticketcoreproject.repository.ArtistaRepo;
import es.iesjuanbosco.ticketcoreproject.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Eventos", description = "Búsqueda y gestión administrativa de eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService; // Inyección del nuevo servicio

    @Autowired
    private ArtistaRepo artistaRepository;

    @GetMapping("/generos")
    @Operation(summary = "Listar géneros", description = "Obtiene lista única de géneros musicales disponibles")
    public ResponseEntity<List<String>> obtenerGeneros() {
        return ResponseEntity.ok(artistaRepository.findGenerosEspecificos());
    }

    @GetMapping("/buscar")
    @Operation(summary = "Búsqueda avanzada", description = "Filtra eventos por ciudad, fecha, keyword y género con paginación")
    public ResponseEntity<Page<EventoDTO>> buscarEventos(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @PageableDefault(size = 9, page = 0) Pageable pageable) {

        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now();
        }

        return ResponseEntity.ok(eventoService.buscarEventos(ciudad, keyword, genero, fechaInicio, pageable));
    }

    // --- ENDPOINTS CRUD (Requeridos para nota completa) ---

    @PostMapping
    @Operation(summary = "Crear Evento (Admin)", description = "Crea un nuevo evento manualmente")
    public ResponseEntity<EventoDTO> crearEvento(@RequestBody EventoDTO eventoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crearEvento(eventoDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Evento (Admin)", description = "Modifica datos de un evento existente")
    public ResponseEntity<EventoDTO> actualizarEvento(@PathVariable Long id, @RequestBody EventoDTO eventoDTO) {
        return ResponseEntity.ok(eventoService.actualizarEvento(id, eventoDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar Evento (Admin)", description = "Elimina un evento de la base de datos")
    public ResponseEntity<Void> borrarEvento(@PathVariable Long id) {
        eventoService.borrarEvento(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> getEvento(@PathVariable Long id) {
        return eventoService.obtenerEventoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}