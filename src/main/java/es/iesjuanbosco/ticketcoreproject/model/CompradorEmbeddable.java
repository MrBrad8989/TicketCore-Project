package es.iesjuanbosco.ticketcoreproject.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
public class CompradorEmbeddable {
    private String nombre;
    private String email;
    private String documentoIdentidad;
    private LocalDate fechaNacimiento;
}
