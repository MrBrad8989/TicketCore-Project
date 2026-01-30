package es.iesjuanbosco.ticketcoreproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private LocalDateTime fechaCompra;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    // Datos del titular/beneficiario del ticket (puede ser distinto al usuario comprador)
    private String compradorNombre;
    private String compradorEmail;
    private String compradorDocumento;
    private LocalDate compradorFechaNacimiento;

    // Relación con la compra que generó este ticket
    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    // Métodos explícitos (además de Lombok) para evitar problemas de reconocimiento en compilación automática
    public Compra getCompra() {
        return this.compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

}