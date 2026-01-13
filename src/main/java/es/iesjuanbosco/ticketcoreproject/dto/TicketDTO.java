package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDTO {
    private Long id;
    private String codigo;
    private LocalDateTime fechaCompra;

    private String tituloEvento;
    private String nombreUsuario;
}