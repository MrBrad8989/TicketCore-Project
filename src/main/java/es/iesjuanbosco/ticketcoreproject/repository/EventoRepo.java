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


    boolean existsByTicketmasterId(String ticketmasterId);

    // Consulta principal para búsqueda con filtros (ciudad, palabra clave, género, fecha)
    // Se permite ciudad vacía/null como "Todas las ciudades".
    @Query("SELECT DISTINCT e FROM Evento e " +
            "LEFT JOIN e.recinto r " +
            "LEFT JOIN e.artistas a " +
            "WHERE (:ciudad IS NULL OR :ciudad = '' OR r.ciudad = :ciudad) " +
            "AND (:keyword IS NULL OR :keyword = '' OR (LOWER(e.titulo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "AND (:genero IS NULL OR :genero = '' OR a.genero = :genero) " +
            "AND (cast(:fecha as timestamp) IS NULL OR e.fechaEvento >= :fecha)")
    Page<Evento> buscarEventos(
            @Param("ciudad") String ciudad,
            @Param("fecha") LocalDateTime fecha,
            @Param("keyword") String keyword,
            @Param("genero") String genero,
            Pageable pageable);

    // Variante que devuelve resultados en orden aleatorio (útil para "todas las ciudades")
    @Query("SELECT DISTINCT e FROM Evento e " +
            "LEFT JOIN e.recinto r " +
            "LEFT JOIN e.artistas a " +
            "WHERE (:ciudad IS NULL OR :ciudad = '' OR r.ciudad = :ciudad) " +
            "AND (:keyword IS NULL OR :keyword = '' OR (LOWER(e.titulo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "AND (:genero IS NULL OR :genero = '' OR a.genero = :genero) " +
            "AND (cast(:fecha as timestamp) IS NULL OR e.fechaEvento >= :fecha) " +
            "ORDER BY function('RAND')")
    Page<Evento> buscarEventosAleatorios(
            @Param("ciudad") String ciudad,
            @Param("fecha") LocalDateTime fecha,
            @Param("keyword") String keyword,
            @Param("genero") String genero,
            Pageable pageable);
}