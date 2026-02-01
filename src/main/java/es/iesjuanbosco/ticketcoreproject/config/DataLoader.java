package es.iesjuanbosco.ticketcoreproject.config;

import es.iesjuanbosco.ticketcoreproject.service.TicketmasterSyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "app.load-data", havingValue = "true", matchIfMissing = true)
public class DataLoader implements CommandLineRunner {

    private final TicketmasterSyncService ticketmasterService;

    public DataLoader(TicketmasterSyncService ticketmasterService) {
        this.ticketmasterService = ticketmasterService;
    }

    @Override
    public void run(String... args) {
        System.out.println("--- Iniciando carga de datos desde Ticketmaster ---");

        List<String> ciudades = List.of("Madrid", "Barcelona", "Valencia", "Bilbao");

        for (String ciudad : ciudades) {
            System.out.println("Sincronizando: " + ciudad);
            ticketmasterService.sincronizarEventos(ciudad);
        }

        System.out.println("--- Carga inicial finalizada ---");
    }
}