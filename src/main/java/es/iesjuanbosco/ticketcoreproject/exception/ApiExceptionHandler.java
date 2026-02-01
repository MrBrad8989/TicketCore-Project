package es.iesjuanbosco.ticketcoreproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({SoldOutException.class, PagoRechazadoException.class, CarritoNotFoundException.class})
    public ResponseEntity<Object> handleCustom(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex instanceof CarritoNotFoundException) status = HttpStatus.NOT_FOUND;
        if (ex instanceof PagoRechazadoException) status = HttpStatus.PAYMENT_REQUIRED;

        return new ResponseEntity<>(body, status);
    }
}
