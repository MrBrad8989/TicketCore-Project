package es.iesjuanbosco.ticketcoreproject.repository;

import es.iesjuanbosco.ticketcoreproject.model.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventoRepo extends JpaRepository<Evento, Long> {

    // Búsqueda avanzada con filtros opcionales
    @Query("SELECT DISTINCT e FROM Evento e " +
            "LEFT JOIN e.artistas a " +
            "WHERE e.recinto.ciudad = :ciudad " +
            "AND e.fechaEvento >= :fecha " +
            "AND (:keyword IS NULL OR :keyword = '' OR LOWER(e.titulo) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:genero IS NULL OR :genero = '' OR LOWER(a.genero) = LOWER(:genero))")
    Page<Evento> buscarEventosAvanzado(
            @Param("ciudad") String ciudad,
            @Param("fecha") LocalDateTime fecha,
            @Param("keyword") String keyword,
            @Param("genero") String genero,
            Pageable pageable
    );

    boolean existsByTicketmasterId(String ticketmasterId);
}