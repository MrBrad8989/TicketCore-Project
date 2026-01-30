package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private String codigo;
    private String fechaCompra;

    private String tituloEvento;
    private String nombreUsuario;

    // Datos del titular/beneficiario del ticket
    private String compradorNombre;
    private String compradorEmail;
    private String compradorDocumento;
    private String compradorFechaNacimiento; // yyyy-MM-dd
}