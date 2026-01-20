package es.iesjuanbosco.ticketcoreproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
// Habilita la serialización de páginas usando DTOs automáticamente
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class TicketCoreProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketCoreProjectApplication.class, args);
    }

}
