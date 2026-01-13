package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

@Data
public class RecintoDTO {
    private Long id;
    private String nombre;
    private String ciudad;
    private Integer aforoMaximo;
}