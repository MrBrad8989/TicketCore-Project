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
    private String imageUrl;

    // Relaciones mostradas como DTOs
    private RecintoDTO recinto;
    private List<ArtistaDTO> artistas;
}