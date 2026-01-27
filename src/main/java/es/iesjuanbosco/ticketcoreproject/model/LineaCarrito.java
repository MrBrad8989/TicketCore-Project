package es.iesjuanbosco.ticketcoreproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LineaCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    private int cantidad;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;
}