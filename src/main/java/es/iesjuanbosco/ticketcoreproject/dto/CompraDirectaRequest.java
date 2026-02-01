package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompraDirectaRequest {
    private Long usuarioId; // opcional
    private CompradorInfoDTO compradorInfo; // datos de facturación si aplica
    private List<LineaCompraInput> lineas;
}
