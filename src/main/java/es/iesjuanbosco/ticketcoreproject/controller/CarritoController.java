package es.iesjuanbosco.ticketcoreproject.controller;

import es.iesjuanbosco.ticketcoreproject.model.Carrito;
import es.iesjuanbosco.ticketcoreproject.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired private CarritoService carritoService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Carrito> getCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
    }

    @PostMapping("/agregar")
    public ResponseEntity<Carrito> agregar(@RequestParam Long usuarioId, @RequestParam Long eventoId, @RequestParam int cantidad) {
        return ResponseEntity.ok(carritoService.agregarItem(usuarioId, eventoId, cantidad));
    }

    @PostMapping("/checkout/{usuarioId}")
    public ResponseEntity<String> checkout(@PathVariable Long usuarioId) {
        try {
            carritoService.finalizarCompra(usuarioId);
            return ResponseEntity.ok("Compra realizada con éxito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}