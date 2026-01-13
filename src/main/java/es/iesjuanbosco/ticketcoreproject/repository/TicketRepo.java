package es.iesjuanbosco.ticketcoreproject.repository;

import es.iesjuanbosco.ticketcoreproject.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long> {

    long countByEventoId(Long eventoId);

    List<Ticket> findByUsuarioId(Long usuarioId);
}