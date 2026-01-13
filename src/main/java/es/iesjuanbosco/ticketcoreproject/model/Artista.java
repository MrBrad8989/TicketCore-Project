package es.iesjuanbosco.ticketcoreproject.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Artista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String genero;

    @ManyToMany(mappedBy = "artistas")
    private List<Evento> eventos;
}