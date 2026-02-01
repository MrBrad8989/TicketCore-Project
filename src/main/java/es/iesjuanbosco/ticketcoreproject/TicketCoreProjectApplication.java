package es.iesjuanbosco.ticketcoreproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
// Enable DTO-based serialization for pageable responses
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class TicketCoreProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketCoreProjectApplication.class, args);
    }

}
