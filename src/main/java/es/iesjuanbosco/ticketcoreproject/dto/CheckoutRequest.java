package es.iesjuanbosco.ticketcoreproject.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long usuarioId; // nullable para invitado
    private CompradorInfoDTO compradorInfo;
    private boolean usarCarrito = true;
}
