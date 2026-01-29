package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;
import java.util.List;

@Data
public class CarritoDTO {
    private Long id;
    private Long usuarioId;
    private List<LineaCarritoDTO> lineas;
}

