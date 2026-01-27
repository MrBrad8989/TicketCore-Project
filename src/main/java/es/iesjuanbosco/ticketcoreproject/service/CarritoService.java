package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.model.*;
import es.iesjuanbosco.ticketcoreproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarritoService {

    @Autowired private CarritoRepo carritoRepo;
    @Autowired private EventoRepo eventoRepo;
    @Autowired private UsuarioRepo usuarioRepo;

    public Carrito obtenerCarrito(Long usuarioId) {

        return carritoRepo.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepo.findById(usuarioId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    return carritoRepo.save(nuevoCarrito);
                });
    }

    @Transactional
    public Carrito agregarItem(Long usuarioId, Long eventoId, int cantidad) {
        Carrito carrito = obtenerCarrito(usuarioId);
        Evento evento = eventoRepo.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Buscar si ya existe el evento en el carrito para sumar cantidad
        Optional<LineaCarrito> lineaExistente = carrito.getLineas().stream()
                .filter(l -> l.getEvento().getId().equals(eventoId))
                .findFirst();

        if (lineaExistente.isPresent()) {
            lineaExistente.get().setCantidad(lineaExistente.get().getCantidad() + cantidad);
        } else {
            LineaCarrito nuevaLinea = new LineaCarrito();
            nuevaLinea.setEvento(evento);
            nuevaLinea.setCantidad(cantidad);
            nuevaLinea.setCarrito(carrito);
            carrito.getLineas().add(nuevaLinea);
        }

        return carritoRepo.save(carrito);
    }

    @Transactional
    public void finalizarCompra(Long usuarioId) {
        Carrito carrito = obtenerCarrito(usuarioId);
        carrito.getLineas().clear();
        carritoRepo.save(carrito);
    }
}