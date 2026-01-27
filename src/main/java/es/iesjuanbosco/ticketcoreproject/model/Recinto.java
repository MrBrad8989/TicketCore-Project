package es.iesjuanbosco.ticketcoreproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Recinto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String ciudad;
    private Integer aforoMaximo;

    @OneToMany(mappedBy = "recinto")
    @JsonIgnore
    private List<Evento> eventos;
}