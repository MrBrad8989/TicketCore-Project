package es.iesjuanbosco.ticketcoreproject.repository;

import es.iesjuanbosco.ticketcoreproject.model.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventoRepo extends JpaRepository<Evento, Long> {
    Page<Evento> findByRecintoCiudadAndFechaEventoAfter(
            String ciudad,
            LocalDateTime fecha,
            Pageable pageable
    );

    Page<Evento> findByArtistasGenero(String genero, Pageable pageable);
}