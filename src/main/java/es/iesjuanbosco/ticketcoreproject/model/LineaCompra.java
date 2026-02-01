package es.iesjuanbosco.ticketcoreproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter @Setter
public class LineaCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    private int cantidad;

    // Precio unitario en el momento de la compra
    private BigDecimal precioUnit;

    // Subtotal = precioUnit * cantidad
    private BigDecimal subtotal;

    @ElementCollection
    @CollectionTable(name = "compradores_linea", joinColumns = @JoinColumn(name = "linea_compra_id"))
    private List<CompradorEmbeddable> compradores = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineaCompra that = (LineaCompra) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
