package es.iesjuanbosco.ticketcoreproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class CarritoNotFoundException extends RuntimeException {
    public CarritoNotFoundException(String mensaje) { super(mensaje); }
}
