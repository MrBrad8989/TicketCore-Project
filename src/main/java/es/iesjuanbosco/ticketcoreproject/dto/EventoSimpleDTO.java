package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

@Data
public class EventoSimpleDTO {
    private Long id;
    private String titulo;
    private Double precio;
    private String imageUrl;
}

