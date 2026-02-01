package es.iesjuanbosco.ticketcoreproject.controller;

import es.iesjuanbosco.ticketcoreproject.dto.EventoDTO;
import es.iesjuanbosco.ticketcoreproject.repository.ArtistaRepo;
import es.iesjuanbosco.ticketcoreproject.service.EventoService;
import es.iesjuanbosco.ticketcoreproject.service.TicketmasterSyncService;
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

    @Autowired
    private TicketmasterSyncService ticketmasterSyncService;

    @GetMapping("/generos")
    @Operation(summary = "Listar géneros", description = "Obtiene lista única de géneros musicales disponibles")
    public ResponseEntity<List<String>> obtenerGeneros() {
        return ResponseEntity.ok(artistaRepository.findGenerosEspecificos());
    }

    @GetMapping("/mis-eventos")
    @Operation(summary = "Mis eventos", description = "Obtiene los eventos creados por el usuario actual")
    public ResponseEntity<List<EventoDTO>> obtenerMisEventos(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(eventoService.obtenerEventosPorCreador(userId));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Búsqueda avanzada", description = "Filtra eventos por ciudad, fecha, keyword y género con paginación")
    public ResponseEntity<Page<EventoDTO>> buscarEventos(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @PageableDefault(size = 9) Pageable pageable) {

        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now();
        }

        return ResponseEntity.ok(eventoService.buscarEventos(ciudad, keyword, genero, fechaInicio, pageable));
    }


    @PostMapping
    @Operation(summary = "Crear Evento (Admin/Empresa)", description = "Crea un nuevo evento; si la petición incluye cabeceras X-User-Id/X-User-Rol y el DTO no tiene creadorId, se asigna automáticamente.")
    public ResponseEntity<EventoDTO> crearEvento(@RequestBody EventoDTO eventoDTO,
                                                  @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // Si no viene creadorId en el DTO y hay un userId, lo asignamos
        if (eventoDTO.getCreadorId() == null && userId != null) {
            eventoDTO.setCreadorId(userId);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crearEvento(eventoDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Evento (Admin/Empresa creadora)", description = "Modifica datos de un evento existente")
    public ResponseEntity<EventoDTO> actualizarEvento(@PathVariable Long id, @RequestBody EventoDTO eventoDTO,
                                                       @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                       @RequestHeader(value = "X-User-Rol", required = false) String userRol) {
        // Admin puede actualizar cualquiera
        if ("ADMIN".equalsIgnoreCase(userRol)) {
            return ResponseEntity.ok(eventoService.actualizarEvento(id, eventoDTO));
        }

        // Empresa solo puede actualizar si es el creador
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        eventoDTO.setCreadorId(userId);
        return ResponseEntity.ok(eventoService.actualizarEvento(id, eventoDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar Evento (Admin/Empresa creadora)", description = "Elimina un evento de la base de datos. Admins pueden borrar cualquiera; empresas solo los suyos.")
    public ResponseEntity<Void> borrarEvento(@PathVariable Long id,
                                              @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                              @RequestHeader(value = "X-User-Rol", required = false) String userRol) {
        eventoService.borrarEvento(id, userId, userRol);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> getEvento(@PathVariable Long id) {
        return eventoService.obtenerEventoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/actualizar-precios")
    @Operation(summary = "Actualizar precios (Admin)", description = "Actualiza los precios de todos los eventos con precio 0 o muy bajo, generando precios aleatorios realistas basados en el género")
    public ResponseEntity<String> actualizarPrecios(@RequestHeader(value = "X-User-Rol", required = false) String userRol) {
        // Solo admins pueden ejecutar esta operación
        if (!"ADMIN".equalsIgnoreCase(userRol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden actualizar precios");
        }

        ticketmasterSyncService.actualizarPreciosExistentes();
        return ResponseEntity.ok("Precios actualizados correctamente");
    }
}