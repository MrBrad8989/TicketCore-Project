package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long usuarioId; // nullable for guest
    private CompradorInfoDTO compradorInfo;
    private boolean usarCarrito = true;
    // future: List<LineaDirectaDTO> lineas;
}
