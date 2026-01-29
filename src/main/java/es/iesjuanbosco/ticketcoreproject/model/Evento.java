package es.iesjuanbosco.ticketcoreproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private LocalDateTime fechaEvento;
    private Double precio;
    @Column(length = 1000)
    private String descripcion;

    @Column(unique = true)
    private String ticketmasterId;



    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "recinto_id")
    private Recinto recinto;

    @ManyToMany
    @JoinTable(
            name = "evento_artista",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "artista_id")
    )
    @JsonIgnore
    private List<Artista> artistas;

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;
}