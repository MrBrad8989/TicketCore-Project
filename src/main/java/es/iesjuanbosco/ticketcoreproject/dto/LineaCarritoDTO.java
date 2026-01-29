package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

@Data
public class LineaCarritoDTO {
    private Long id;
    private EventoSimpleDTO evento;
    private int cantidad;
}

