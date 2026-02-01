package es.iesjuanbosco.ticketcoreproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    private BigDecimal total;

    private String estado; // PENDING, PAID, CANCELLED

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // nullable para invitado

    private String compradorNombre;
    private String compradorEmail;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaCompra> lineas = new ArrayList<>();

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    private String referenciaPago;
}
