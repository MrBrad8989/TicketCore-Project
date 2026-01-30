package es.iesjuanbosco.ticketcoreproject.repository;

import es.iesjuanbosco.ticketcoreproject.model.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistaRepo extends JpaRepository<Artista, Long> {
    @Query("SELECT DISTINCT a.genero FROM Artista a WHERE a.genero IS NOT NULL ORDER BY a.genero ASC")
    List<String> findGenerosEspecificos();

    // Buscar artista por nombre (ignorando mayúsculas/minúsculas)
    Artista findByNombreIgnoreCase(String nombre);
}