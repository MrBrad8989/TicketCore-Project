package es.iesjuanbosco.ticketcoreproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailTemplateService {

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Genera el HTML del email de ticket usando la plantilla Thymeleaf
     */
    public String generarEmailTicket(String compradorNombre, String eventoTitulo, String fechaEvento, 
                                      String recinto, String ciudad, String codigoTicket, String referenciaPago) {
        Context context = new Context(new Locale("es", "ES"));
        
        // Variables para la plantilla
        context.setVariable("compradorNombre", compradorNombre != null ? compradorNombre : "Usuario");
        context.setVariable("eventoTitulo", eventoTitulo != null ? eventoTitulo : "Evento sin título");
        context.setVariable("fechaEvento", fechaEvento != null ? fechaEvento : "Fecha por confirmar");
        context.setVariable("recinto", recinto);
        context.setVariable("ciudad", ciudad);
        context.setVariable("codigoTicket", codigoTicket != null ? codigoTicket : "N/A");
        context.setVariable("referenciaPago", referenciaPago != null ? referenciaPago : "N/A");
        
        return templateEngine.process("email-ticket", context);
    }

    /**
     * Formatea una fecha para mostrar en el email
     */
    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "Fecha por confirmar";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");
        return fecha.format(formatter);
    }
}

