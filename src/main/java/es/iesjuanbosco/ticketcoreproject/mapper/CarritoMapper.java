package es.iesjuanbosco.ticketcoreproject.mapper;

import es.iesjuanbosco.ticketcoreproject.dto.CarritoDTO;
import es.iesjuanbosco.ticketcoreproject.dto.EventoSimpleDTO;
import es.iesjuanbosco.ticketcoreproject.dto.LineaCarritoDTO;
import es.iesjuanbosco.ticketcoreproject.model.Carrito;
import es.iesjuanbosco.ticketcoreproject.model.LineaCarrito;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarritoMapper {

    @Mapping(source = "lineas", target = "lineas")
    CarritoDTO toDTO(Carrito carrito);

    List<LineaCarritoDTO> lineasToDTOs(List<LineaCarrito> lineas);

    // Mapstruct needs a mapping for a single LineaCarrito -> LineaCarritoDTO
    LineaCarritoDTO lineaToDTO(LineaCarrito linea);

    EventoSimpleDTO eventoToSimpleDTO(Evento evento);
}
