package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.model.*;
import es.iesjuanbosco.ticketcoreproject.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
public class CompraServiceTest {

    @Autowired
    private CompraService compraService;

    @Autowired
    private RecintoRepo recintoRepo;

    @Autowired
    private EventoRepo eventoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private CarritoRepo carritoRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private CompraRepo compraRepo;

    @Test
    public void crearCompraDesdeCarrito_happyPath() {
        // Crear recinto
        Recinto recinto = new Recinto();
        recinto.setNombre("Sala Test");
        recinto.setCiudad("Ciudad");
        recinto.setAforoMaximo(100);
        recinto = recintoRepo.save(recinto);

        // Crear evento
        Evento evento = new Evento();
        evento.setTitulo("Concierto Test");
        evento.setPrecio(15.0);
        evento.setRecinto(recinto);
        evento = eventoRepo.save(evento);

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre("Alice");
        usuario.setEmail("alice@example.com");
        usuario = usuarioRepo.save(usuario);

        // Crear carrito con una linea
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);

        LineaCarrito linea = new LineaCarrito();
        linea.setEvento(evento);
        linea.setCantidad(3);
        linea.setCarrito(carrito);

        carrito.getLineas().add(linea);
        carrito = carritoRepo.save(carrito);

        // Ejecutar compra
        compraService.crearCompraDesdeCarrito(usuario.getId(), null);

        // Verificaciones
        List<Ticket> tickets = ticketRepo.findByUsuarioId(usuario.getId());
        Assertions.assertEquals(3, tickets.size(), "Deben generarse 3 tickets");

        long compras = compraRepo.count();
        Assertions.assertEquals(1, compras, "Debe existir una compra");

        Carrito carritoAfter = carritoRepo.findByUsuarioId(usuario.getId()).orElse(null);
        Assertions.assertNotNull(carritoAfter);
        Assertions.assertTrue(carritoAfter.getLineas().isEmpty(), "El carrito debe quedar vacío tras la compra");
    }
}
