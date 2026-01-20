package es.iesjuanbosco.ticketcoreproject.mapper;

import es.iesjuanbosco.ticketcoreproject.dto.TicketDTO;
import es.iesjuanbosco.ticketcoreproject.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    // Mapeo personalizado para campos con nombres diferentes
    @Mapping(source = "evento.titulo", target = "tituloEvento")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    TicketDTO toDTO(Ticket ticket);
}