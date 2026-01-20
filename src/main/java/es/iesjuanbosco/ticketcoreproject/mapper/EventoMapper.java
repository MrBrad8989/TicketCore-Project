package es.iesjuanbosco.ticketcoreproject.mapper;

import es.iesjuanbosco.ticketcoreproject.dto.ArtistaDTO;
import es.iesjuanbosco.ticketcoreproject.dto.EventoDTO;
import es.iesjuanbosco.ticketcoreproject.dto.RecintoDTO;
import es.iesjuanbosco.ticketcoreproject.model.Artista;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import es.iesjuanbosco.ticketcoreproject.model.Recinto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventoMapper {

    // MapStruct detecta automáticamente los campos con el mismo nombre
    EventoDTO toDTO(Evento evento);

    Evento toEntity(EventoDTO eventoDTO);

    // Mapeos para las sub-entidades
    RecintoDTO toRecintoDTO(Recinto recinto);
    ArtistaDTO toArtistaDTO(Artista artista);
}