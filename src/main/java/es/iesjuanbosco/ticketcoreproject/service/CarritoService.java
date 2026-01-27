package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.model.*;
import es.iesjuanbosco.ticketcoreproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CarritoService {

    @Autowired private CarritoRepo carritoRepo;
    @Autowired private UsuarioRepo usuarioRepo;
    @Autowired private EventoRepo eventoRepo;
    @Autowired private TicketRepo ticketRepo;

    // Obtener o crear carrito del usuario
    public Carrito obtenerCarrito(Long usuarioId) {
        return carritoRepo.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario u = usuarioRepo.findById(usuarioId).orElseThrow();
                    Carrito c = new Carrito();
                    c.setUsuario(u);
                    return carritoRepo.save(c);
                });
    }

    // Añadir item
    public Carrito agregarItem(Long usuarioId, Long eventoId, int cantidad) {
        Carrito carrito = obtenerCarrito(usuarioId);
        Evento evento = eventoRepo.findById(eventoId).orElseThrow();

        // Buscamos si ya existe el evento en el carrito para sumar cantidad
        LineaCarrito lineaExistente = carrito.getLineas().stream()
                .filter(l -> l.getEvento().getId().equals(eventoId))
                .findFirst().orElse(null);

        if (lineaExistente != null) {
            lineaExistente.setCantidad(lineaExistente.getCantidad() + cantidad);
        } else {
            LineaCarrito nuevaLinea = new LineaCarrito();
            nuevaLinea.setEvento(evento);
            nuevaLinea.setCantidad(cantidad);
            nuevaLinea.setCarrito(carrito);
            carrito.getLineas().add(nuevaLinea);
        }

        return carritoRepo.save(carrito);
    }

    // Vaciar carrito
    public void vaciarCarrito(Long usuarioId) {
        Carrito c = obtenerCarrito(usuarioId);
        c.getLineas().clear();
        carritoRepo.save(c);
    }

    // FINALIZAR COMPRA (Convierte Items -> Tickets Reales)
    @Transactional
    public void finalizarCompra(Long usuarioId) {
        Carrito carrito = obtenerCarrito(usuarioId);
        if(carrito.getLineas().isEmpty()) throw new RuntimeException("El carrito está vacío");

        for (LineaCarrito linea : carrito.getLineas()) {
            // Por cada unidad, creamos un ticket
            for (int i = 0; i < linea.getCantidad(); i++) {
                Ticket ticket = new Ticket();
                ticket.setUsuario(carrito.getUsuario());
                ticket.setEvento(linea.getEvento());
                ticket.setFechaCompra(LocalDateTime.now());
                ticketRepo.save(ticket);
            }
        }
        // Vaciamos el carrito tras la compra
        carrito.getLineas().clear();
        carritoRepo.save(carrito);
    }
}