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

    // Map usuario.id -> usuarioId and map lineas collection
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "lineas", target = "lineas")
    CarritoDTO toDTO(Carrito carrito);

    @SuppressWarnings("unused")
    List<LineaCarritoDTO> lineasToDTOs(List<LineaCarrito> lineas);

    @SuppressWarnings("unused")
    @Mapping(source = "evento", target = "evento")
    LineaCarritoDTO lineaToDTO(LineaCarrito linea);

    @SuppressWarnings("unused")
    EventoSimpleDTO eventoToSimpleDTO(Evento evento);
}
