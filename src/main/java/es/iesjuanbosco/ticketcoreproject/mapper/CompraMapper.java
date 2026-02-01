package es.iesjuanbosco.ticketcoreproject.mapper;

import es.iesjuanbosco.ticketcoreproject.dto.CompraDTO;
import es.iesjuanbosco.ticketcoreproject.model.Compra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TicketMapper.class})
public interface CompraMapper {

    @Mapping(source = "referenciaPago", target = "referenciaPago")
    CompraDTO toDTO(Compra compra);
}
