package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CompraDTO {
    private Long id;
    private String referenciaPago;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String compradorNombre;
    private String compradorEmail;
    private List<TicketDTO> tickets;
}
