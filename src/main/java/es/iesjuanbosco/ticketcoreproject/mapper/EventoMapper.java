package es.iesjuanbosco.ticketcoreproject.mapper;

import es.iesjuanbosco.ticketcoreproject.dto.ArtistaDTO;
import es.iesjuanbosco.ticketcoreproject.dto.EventoDTO;
import es.iesjuanbosco.ticketcoreproject.dto.RecintoDTO;
import es.iesjuanbosco.ticketcoreproject.model.Artista;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import es.iesjuanbosco.ticketcoreproject.model.Recinto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EventoMapper {

    // Mapstruct detecta automáticamente los campos con el mismo nombre
    @Mapping(source = "creador", target = "creadorId", qualifiedByName = "mapCreadorToId")
    @Mapping(source = "maxEntradas", target = "maxEntradas")
    EventoDTO toDTO(Evento evento);

    @Mapping(source = "creadorId", target = "creador", qualifiedByName = "mapIdToCreador")
    @Mapping(source = "maxEntradas", target = "maxEntradas")
    Evento toEntity(EventoDTO eventoDTO);

    // Mapeos para las subentidades
    RecintoDTO toRecintoDTO(Recinto recinto);
    ArtistaDTO toArtistaDTO(Artista artista);

    // Mapear creador.Id manualmente
    @Named("mapCreadorToId")
    @SuppressWarnings("unused")
    default Long mapCreadorToId(es.iesjuanbosco.ticketcoreproject.model.Usuario creador) {
        return creador == null ? null : creador.getId();
    }

    @Named("mapIdToCreador")
    @SuppressWarnings("unused")
    default es.iesjuanbosco.ticketcoreproject.model.Usuario mapIdToCreador(Long id) {
        if (id == null) return null;
        es.iesjuanbosco.ticketcoreproject.model.Usuario u = new es.iesjuanbosco.ticketcoreproject.model.Usuario();
        u.setId(id);
        return u;
    }
}