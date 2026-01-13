package es.iesjuanbosco.ticketcoreproject.repository;

import es.iesjuanbosco.ticketcoreproject.model.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistaRepo extends JpaRepository<Artista, Long> {
}