package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompraDirectaRequest {
    private Long usuarioId; // opcional (guest purchases allowed)
    private CompradorInfoDTO compradorInfo; // datos de facturación opcionales
    private List<LineaCompraInput> lineas;
}
