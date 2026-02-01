package es.iesjuanbosco.ticketcoreproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.iesjuanbosco.ticketcoreproject.model.Evento;
import es.iesjuanbosco.ticketcoreproject.model.Recinto;
import es.iesjuanbosco.ticketcoreproject.model.Usuario;
import es.iesjuanbosco.ticketcoreproject.repository.EventoRepo;
import es.iesjuanbosco.ticketcoreproject.repository.RecintoRepo;
import es.iesjuanbosco.ticketcoreproject.repository.UsuarioRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CompraZipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecintoRepo recintoRepo;

    @Autowired
    private EventoRepo eventoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void compraDirecta_confirm_and_zipContainsTickets() throws Exception {
        // Crear recinto
        Recinto r = new Recinto();
        r.setNombre("Recinto Test");
        r.setCiudad("TestCity");
        r.setAforoMaximo(100);
        r = recintoRepo.save(r);

        // Crear evento
        Evento e = new Evento();
        e.setTitulo("Evento Test");
        e.setRecinto(r);
        e.setFechaEvento(LocalDateTime.now().plusDays(10));
        e.setPrecio(10.0);
        e = eventoRepo.save(e);

        // Crear usuario
        Usuario u = new Usuario();
        u.setNombre("Test User");
        u.setEmail("test@example.com");
        u.setPassword("x");
        u = usuarioRepo.save(u);

        // Crear compra directa con 2 compradores
        String payload = "{\"usuarioId\":null,\"compradorInfo\":{\"nombre\":\"Org\",\"email\":\"org@example.com\"},\"lineas\":[{\"eventoId\":" + e.getId() + ",\"cantidad\":2,\"compradores\":[{\"nombre\":\"A\",\"email\":\"a@e.com\",\"documentoIdentidad\":\"D1\",\"fechaNacimiento\":\"1990-01-01\"},{\"nombre\":\"B\",\"email\":\"b@e.com\",\"documentoIdentidad\":\"D2\",\"fechaNacimiento\":\"1991-02-02\"}]}]}";

        // Llamar endpoint /api/compras/directo
        String resp = mockMvc.perform(post("/api/compras/directo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> compra = objectMapper.readValue(resp, Map.class);
        Integer compraId = (Integer) compra.get("id");
        assertThat(compraId).isNotNull();

        // Confirmar pago
        String confirmResp = mockMvc.perform(post("/api/pagos/" + compraId + "/confirm"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Descargar ZIP
        byte[] zipBytes = mockMvc.perform(get("/api/compras/" + compraId + "/zip"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // Verificar que el ZIP contiene 2 entradas (tickets)
        try (ZipInputStream zis = new ZipInputStream(new java.io.ByteArrayInputStream(zipBytes))) {
            int count = 0;
            while (zis.getNextEntry() != null) {
                count++;
            }
            assertThat(count).isEqualTo(2);
        }
    }
}
