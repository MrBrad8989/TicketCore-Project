package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class LineaCompraInput {
    private Long eventoId;
    private int cantidad;
    private List<CompradorInfoDTO> compradores; // opcional: datos por cada ticket
}
