package es.iesjuanbosco.ticketcoreproject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesjuanbosco.ticketcoreproject.model.Artista;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import es.iesjuanbosco.ticketcoreproject.model.Recinto;
import es.iesjuanbosco.ticketcoreproject.repository.ArtistaRepo;
import es.iesjuanbosco.ticketcoreproject.repository.EventoRepo;
import es.iesjuanbosco.ticketcoreproject.repository.RecintoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TicketmasterSyncService {

    @Value("${ticketmaster.api.key}")
    private String apiKey;

    @Autowired
    private EventoRepo eventoRepo;
    @Autowired
    private RecintoRepo recintoRepo;
    @Autowired
    private ArtistaRepo artistaRepo;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sincronizarEventos(String ciudad) {
        // 1. URL filtrada por Música y con tamaño 100 para tener margen de variedad
        String url = "https://app.ticketmaster.com/discovery/v2/events.json?city=" + ciudad +
                "&segmentName=Music&apikey=" + apiKey + "&size=100";

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (!root.has("_embedded")) {
                System.out.println("No se encontraron eventos en Ticketmaster para " + ciudad);
                return;
            }

            JsonNode eventsNode = root.path("_embedded").path("events");

            // 2. SET para controlar duplicados de NOMBRE en esta carga (Variedad)
            Set<String> titulosProcesados = new HashSet<>();

            for (JsonNode eventNode : eventsNode) {
                String titulo = eventNode.path("name").asText();

                // Si ya guardamos un evento con este nombre en este ciclo, lo saltamos
                if (titulosProcesados.contains(titulo)) {
                    continue;
                }

                try {
                    // Procesamos el evento
                    procesarEvento(eventNode, ciudad);

                    // Si no hubo error, lo marcamos como procesado para no repetir
                    titulosProcesados.add(titulo);

                } catch (Exception e) {
                    System.err.println("Error importando un evento: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesarEvento(JsonNode node, String ciudadDefault) {
        String tmId = node.path("id").asText();

        // Evitar duplicados por ID (si ya está en BD, no hacemos nada)
        if (eventoRepo.existsByTicketmasterId(tmId)) {
            System.out.println("El evento " + tmId + " ya existe en BD. Saltando...");
            return;
        }

        // --- Recinto ---
        JsonNode venues = node.path("_embedded").path("venues");
        String nombreRecinto = "Recinto Desconocido";
        if (venues.isArray() && venues.size() > 0) {
            nombreRecinto = venues.get(0).path("name").asText();
        }

        String nombreRecintoFinal = nombreRecinto;

        Recinto recinto = null;
        Optional<Recinto> recintoOpt = recintoRepo.findAll().stream()
                .filter(r -> r.getNombre().equalsIgnoreCase(nombreRecintoFinal)).findFirst();

        if (recintoOpt.isPresent()) {
            recinto = recintoOpt.get();
        } else {
            recinto = new Recinto();
            recinto.setNombre(nombreRecinto);
            recinto.setCiudad(ciudadDefault);
            recinto.setAforoMaximo(5000);
            recinto = recintoRepo.save(recinto);
        }

        // --- Artistas ---
        List<Artista> artistasEvento = new ArrayList<>();
        JsonNode attractions = node.path("_embedded").path("attractions");
        if (attractions.isArray()) {
            for (JsonNode att : attractions) {
                String nombreArtista = att.path("name").asText();
                String finalNombreArtista = nombreArtista;

                Optional<Artista> artistaOpt = artistaRepo.findAll().stream()
                        .filter(a -> a.getNombre().equalsIgnoreCase(finalNombreArtista)).findFirst();

                Artista artista;
                if (artistaOpt.isPresent()) {
                    artista = artistaOpt.get();
                } else {
                    String genero = "General";
                    JsonNode classifications = att.path("classifications");
                    // Intentar obtener género si está definido
                    if (classifications.isArray() && classifications.size() > 0) {
                        JsonNode genreNode = classifications.get(0).path("genre").path("name");
                        if (!genreNode.isMissingNode() && !genreNode.asText().equalsIgnoreCase("Undefined")) {
                            genero = genreNode.asText();
                        }
                    }
                    artista = new Artista();
                    artista.setNombre(nombreArtista);
                    artista.setGenero(genero);
                    artista = artistaRepo.save(artista);
                }
                artistasEvento.add(artista);
            }
        }

        // --- Crear Evento ---
        Evento evento = new Evento();
        evento.setTicketmasterId(tmId);
        evento.setTitulo(node.path("name").asText());

        String fechaStr = node.path("dates").path("start").path("dateTime").asText();
        if (fechaStr != null && !fechaStr.isEmpty()) {
            try {
                evento.setFechaEvento(LocalDateTime.parse(fechaStr, DateTimeFormatter.ISO_DATE_TIME));
            } catch (Exception e) {
                evento.setFechaEvento(LocalDateTime.now().plusDays(15));
            }
        } else {
            evento.setFechaEvento(LocalDateTime.now().plusDays(15));
        }

        JsonNode priceRanges = node.path("priceRanges");
        if (priceRanges.isArray() && priceRanges.size() > 0) {
            evento.setPrecio(priceRanges.get(0).path("min").asDouble());
        } else {
            evento.setPrecio(0.0);
        }

        JsonNode images = node.path("images");
        if (images.isArray() && images.size() > 0) {
            evento.setImageUrl(images.get(0).path("url").asText());
        }

        evento.setRecinto(recinto);
        evento.setArtistas(artistasEvento);

        eventoRepo.save(evento);
        System.out.println("--> Importado evento: " + evento.getTitulo());
    }
}