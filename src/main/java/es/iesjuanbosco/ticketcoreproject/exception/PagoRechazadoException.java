package es.iesjuanbosco.ticketcoreproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PAYMENT_REQUIRED)
public class PagoRechazadoException extends RuntimeException {
    public PagoRechazadoException(String mensaje) { super(mensaje); }
}
