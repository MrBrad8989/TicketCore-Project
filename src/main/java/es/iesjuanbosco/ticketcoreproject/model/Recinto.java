package es.iesjuanbosco.ticketcoreproject.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Recinto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String ciudad;
    private Integer aforoMaximo;

    @OneToMany(mappedBy = "recinto")
    private List<Evento> eventos;
}