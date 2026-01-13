package es.iesjuanbosco.ticketcoreproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Al lanzar esta excepción, la API devolverá un 400 BAD REQUEST automáticamente
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SoldOutException extends RuntimeException {

    public SoldOutException(String mensaje) {
        super(mensaje);
    }
}