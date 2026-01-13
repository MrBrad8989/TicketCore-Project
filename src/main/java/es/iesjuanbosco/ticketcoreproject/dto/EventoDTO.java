package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventoDTO {
    private Long id;
    private String titulo;
    private LocalDateTime fechaEvento;
    private Double precio;

    // Relaciones mostradas como DTOs planos
    private RecintoDTO recinto;
    private List<ArtistaDTO> artistas;
}