package es.iesjuanbosco.ticketcoreproject.config;

import es.iesjuanbosco.ticketcoreproject.model.Artista;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import es.iesjuanbosco.ticketcoreproject.model.Recinto;
import es.iesjuanbosco.ticketcoreproject.repository.ArtistaRepo;
import es.iesjuanbosco.ticketcoreproject.repository.EventoRepo;
import es.iesjuanbosco.ticketcoreproject.repository.RecintoRepo;
import es.iesjuanbosco.ticketcoreproject.service.TicketmasterSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

    private final TicketmasterSyncService ticketmasterService;

    // Inyección de dependencias
    public DataLoader(TicketmasterSyncService ticketmasterService) {
        this.ticketmasterService = ticketmasterService;
    }

    @Override
    public void run(String... args) {
        System.out.println("Iniciando carga de datos desde Ticketmaster...");
        ticketmasterService.sincronizarEventos("Madrid");
        System.out.println("Carga finalizada.");
    }
}