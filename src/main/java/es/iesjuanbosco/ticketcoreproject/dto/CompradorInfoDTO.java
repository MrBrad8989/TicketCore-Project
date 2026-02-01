package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

@Data
public class CompradorInfoDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String documentoIdentificacion;
    private String fechaNacimiento; // yyyy-MM-dd
}
