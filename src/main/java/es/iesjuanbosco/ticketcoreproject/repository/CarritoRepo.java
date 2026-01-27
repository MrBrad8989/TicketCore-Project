package es.iesjuanbosco.ticketcoreproject.repository;
import es.iesjuanbosco.ticketcoreproject.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepo extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}