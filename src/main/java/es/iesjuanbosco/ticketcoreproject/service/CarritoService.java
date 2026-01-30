package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.dto.CompradorInfoDTO;
import es.iesjuanbosco.ticketcoreproject.dto.CarritoDTO;
import es.iesjuanbosco.ticketcoreproject.model.*;
import es.iesjuanbosco.ticketcoreproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import es.iesjuanbosco.ticketcoreproject.mapper.CarritoMapper;

@Service
public class CarritoService {

    @Autowired private CarritoRepo carritoRepo;
    @Autowired private EventoRepo eventoRepo;
    @Autowired private UsuarioRepo usuarioRepo;
    @Autowired private CarritoMapper carritoMapper;

    // Inyectamos CompraService para delegar el checkout
    @Autowired private CompraService compraService;

    public CarritoDTO obtenerCarrito(Long usuarioId) {
        Carrito c = carritoRepo.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepo.findById(usuarioId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    return carritoRepo.save(nuevoCarrito);
                });

        return carritoMapper.toDTO(c);
    }

    @Transactional
    public CarritoDTO agregarItem(Long usuarioId, Long eventoId, int cantidad) {
        Carrito carrito = obtenerCarritoEntity(usuarioId);
        Evento evento = eventoRepo.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

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

        carrito = carritoRepo.save(carrito);
        return carritoMapper.toDTO(carrito);
    }

    @Transactional
    public void finalizarCompra(Long usuarioId) {
        // Delegar al servicio de compras para crear Compra y Tickets
        CompradorInfoDTO compradorInfo = new CompradorInfoDTO();
        // Si se quiere obtener datos del usuario para autocompletar, se podría rellenar compradorInfo aquí
        compraService.crearCompraDesdeCarrito(usuarioId, compradorInfo);
    }

    @Transactional
    public CarritoDTO disminuirItem(Long usuarioId, Long eventoId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a disminuir debe ser mayor que 0");
        Carrito carrito = obtenerCarritoEntity(usuarioId);

        Optional<LineaCarrito> lineaExistente = carrito.getLineas().stream()
                .filter(l -> l.getEvento().getId().equals(eventoId))
                .findFirst();

        if (lineaExistente.isEmpty()) {
            throw new RuntimeException("Línea no encontrada en el carrito");
        }

        LineaCarrito linea = lineaExistente.get();
        int nuevaCantidad = linea.getCantidad() - cantidad;
        if (nuevaCantidad > 0) {
            linea.setCantidad(nuevaCantidad);
        } else {
            // eliminar la línea
            carrito.getLineas().remove(linea);
        }

        carrito = carritoRepo.save(carrito);
        return carritoMapper.toDTO(carrito);
    }

    @Transactional
    public CarritoDTO eliminarLinea(Long usuarioId, Long lineaId) {
        Carrito carrito = obtenerCarritoEntity(usuarioId);

        Optional<LineaCarrito> lineaExistente = carrito.getLineas().stream()
                .filter(l -> l.getId() != null && l.getId().equals(lineaId))
                .findFirst();

        if (lineaExistente.isEmpty()) {
            throw new RuntimeException("Línea no encontrada en el carrito");
        }

        carrito.getLineas().remove(lineaExistente.get());
        carrito = carritoRepo.save(carrito);
        return carritoMapper.toDTO(carrito);
    }

    // Helpers
    private Carrito obtenerCarritoEntity(Long usuarioId) {
        return carritoRepo.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepo.findById(usuarioId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    return carritoRepo.save(nuevoCarrito);
                });
    }
}