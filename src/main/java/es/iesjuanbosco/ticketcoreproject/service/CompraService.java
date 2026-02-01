package es.iesjuanbosco.ticketcoreproject.service;

import es.iesjuanbosco.ticketcoreproject.dto.CompradorInfoDTO;
import es.iesjuanbosco.ticketcoreproject.dto.CompraDirectaRequest;
import es.iesjuanbosco.ticketcoreproject.dto.LineaCompraInput;
import es.iesjuanbosco.ticketcoreproject.exception.CarritoNotFoundException;
import es.iesjuanbosco.ticketcoreproject.exception.SoldOutException;
import es.iesjuanbosco.ticketcoreproject.model.*;
import es.iesjuanbosco.ticketcoreproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompraService {

    @Autowired
    private CarritoRepo carritoRepo;

    @Autowired
    private CompraRepo compraRepo;

    @Autowired
    private LineaCompraRepo lineaCompraRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private EventoRepo eventoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    private CompradorEmbeddable mapDtoToEmbeddable(CompradorInfoDTO dto) {
        if (dto == null) return null;
        CompradorEmbeddable c = new CompradorEmbeddable();
        c.setNombre(dto.getNombre());
        c.setEmail(dto.getEmail());
        // DTO field is documentoIdentificacion, embeddable uses documentoIdentidad
        c.setDocumentoIdentidad(dto.getDocumentoIdentificacion());
        // fechaNacimiento in DTO is a String (yyyy-MM-dd) -> parse to LocalDate
        if (dto.getFechaNacimiento() != null && !dto.getFechaNacimiento().isBlank()) {
            try {
                c.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
            } catch (DateTimeParseException ex) {
                // Si hay un formato inválido, dejamos la fecha a null (no bloqueante)
            }
        }
        return c;
    }

    @Transactional
    public Compra crearCompraDesdeCarrito(Long usuarioId, CompradorInfoDTO compradorInfo) {
        // Recuperar carrito
        Carrito carrito = carritoRepo.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new CarritoNotFoundException("Carrito no encontrado para usuario: " + usuarioId));

        if (carrito.getLineas() == null || carrito.getLineas().isEmpty()) {
            throw new IllegalArgumentException("Carrito vacío");
        }

        // Crear Compra en estado PENDING (esperando pago)
        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setEstado("PENDING");
        compra.setUsuario(carrito.getUsuario());
        if (compradorInfo != null) {
            compra.setCompradorNombre(compradorInfo.getNombre());
            compra.setCompradorEmail(compradorInfo.getEmail());
        } else if (carrito.getUsuario() != null) {
            compra.setCompradorNombre(carrito.getUsuario().getNombre());
            compra.setCompradorEmail(carrito.getUsuario().getEmail());
        }

        BigDecimal total = BigDecimal.ZERO;

        // Persistir compra primero para establecer relaciones
        Compra compraGuardada = compraRepo.save(compra);

        // Por cada linea de carrito crear lineaCompra (sin generar tickets todavía)
        for (LineaCarrito lc : carrito.getLineas()) {
            Evento evento = eventoRepo.findById(lc.getEvento().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado: " + lc.getEvento().getId()));

            // Validar aforo disponible (reserva no definitiva)
            long vendidos = ticketRepo.countByEventoId(evento.getId());
            int aforo = evento.getRecinto() != null && evento.getRecinto().getAforoMaximo() != null ? evento.getRecinto().getAforoMaximo() : Integer.MAX_VALUE;

            if (vendidos + lc.getCantidad() > aforo) {
                throw new SoldOutException("No hay suficiente aforo para el evento: " + evento.getTitulo());
            }

            LineaCompra lineaCompra = new LineaCompra();
            lineaCompra.setCompra(compraGuardada);
            lineaCompra.setEvento(evento);
            lineaCompra.setCantidad(lc.getCantidad());
            lineaCompra.setPrecioUnit(BigDecimal.valueOf(evento.getPrecio()));
            lineaCompra.setSubtotal(lineaCompra.getPrecioUnit().multiply(BigDecimal.valueOf(lineaCompra.getCantidad())));

            // Si tenemos datos de comprador general, repetirlos para cada ticket
            if (compradorInfo != null) {
                CompradorEmbeddable emb = mapDtoToEmbeddable(compradorInfo);
                for (int i = 0; i < lineaCompra.getCantidad(); i++) lineaCompra.getCompradores().add(emb);
            } else if (compraGuardada.getUsuario() != null) {
                // Autorellenar con datos del usuario si existen
                Usuario u = compraGuardada.getUsuario();
                CompradorEmbeddable emb = new CompradorEmbeddable();
                emb.setNombre(u.getNombre());
                emb.setEmail(u.getEmail());
                for (int i = 0; i < lineaCompra.getCantidad(); i++) lineaCompra.getCompradores().add(emb);
            }

            LineaCompra lineaGuardada = lineaCompraRepo.save(lineaCompra);

            total = total.add(lineaGuardada.getSubtotal());
        }

        compraGuardada.setTotal(total);
        compraGuardada.setReferenciaPago(UUID.randomUUID().toString());
        compraRepo.save(compraGuardada);

        // Nota: no se generan tickets hasta confirmar el pago

        return compraGuardada;
    }

    @Transactional
    public Compra crearCompraDirecta(CompraDirectaRequest req) {
        if (req.getLineas() == null || req.getLineas().isEmpty()) {
            throw new IllegalArgumentException("No se han indicado líneas de compra");
        }

        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setEstado("PENDING");
        if (req.getCompradorInfo() != null) {
            compra.setCompradorNombre(req.getCompradorInfo().getNombre());
            compra.setCompradorEmail(req.getCompradorInfo().getEmail());
        }
        if (req.getUsuarioId() != null) {
            usuarioRepo.findById(req.getUsuarioId()).ifPresent(compra::setUsuario);
        }

        Compra compraGuardada = compraRepo.save(compra);
        BigDecimal total = BigDecimal.ZERO;

        for (LineaCompraInput l : req.getLineas()) {
            Evento evento = eventoRepo.findById(l.getEventoId()).orElseThrow(() -> new IllegalArgumentException("Evento no encontrado: " + l.getEventoId()));

            long vendidos = ticketRepo.countByEventoId(evento.getId());
            int aforo = evento.getRecinto() != null && evento.getRecinto().getAforoMaximo() != null ? evento.getRecinto().getAforoMaximo() : Integer.MAX_VALUE;
            if (vendidos + l.getCantidad() > aforo) throw new SoldOutException("No hay suficiente aforo para el evento: " + evento.getTitulo());

            LineaCompra lineaCompra = new LineaCompra();
            lineaCompra.setCompra(compraGuardada);
            lineaCompra.setEvento(evento);
            lineaCompra.setCantidad(l.getCantidad());
            lineaCompra.setPrecioUnit(BigDecimal.valueOf(evento.getPrecio()));
            lineaCompra.setSubtotal(lineaCompra.getPrecioUnit().multiply(BigDecimal.valueOf(lineaCompra.getCantidad())));
            LineaCompra lineaGuardada = lineaCompraRepo.save(lineaCompra);

            // Mapear compradores proporcionados en la request
            if (l.getCompradores() != null && !l.getCompradores().isEmpty()) {
                List<CompradorEmbeddable> lista = l.getCompradores().stream()
                        .map(this::mapDtoToEmbeddable)
                        .collect(Collectors.toList());
                // Si la lista es menor que la cantidad, rellenar con compradorInfo general si existe
                if (lista.size() < lineaGuardada.getCantidad() && req.getCompradorInfo() != null) {
                    CompradorEmbeddable fill = mapDtoToEmbeddable(req.getCompradorInfo());
                    while (lista.size() < lineaGuardada.getCantidad()) lista.add(fill);
                }
                lineaGuardada.getCompradores().addAll(lista);
                lineaCompraRepo.save(lineaGuardada);
            } else if (req.getCompradorInfo() != null) {
                CompradorEmbeddable emb = mapDtoToEmbeddable(req.getCompradorInfo());
                for (int i = 0; i < lineaGuardada.getCantidad(); i++) lineaGuardada.getCompradores().add(emb);
                lineaCompraRepo.save(lineaGuardada);
            }

            total = total.add(lineaGuardada.getSubtotal());
        }

        compraGuardada.setTotal(total);
        compraGuardada.setReferenciaPago(UUID.randomUUID().toString());
        compraRepo.save(compraGuardada);

        return compraGuardada;
    }

    @Transactional
    public Compra confirmarCompra(Long compraId) {
        Compra compra = compraRepo.findById(compraId).orElseThrow(() -> new IllegalArgumentException("Compra no encontrada: " + compraId));
        if (!"PENDING".equalsIgnoreCase(compra.getEstado())) {
            throw new IllegalStateException("La compra no está en estado PENDING");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (LineaCompra lc : compra.getLineas()) {
            Evento evento = eventoRepo.findById(lc.getEvento().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado: " + lc.getEvento().getId()));

            // Validar aforo final antes de generar tickets
            long vendidos = ticketRepo.countByEventoId(evento.getId());
            int aforo = evento.getRecinto() != null && evento.getRecinto().getAforoMaximo() != null ? evento.getRecinto().getAforoMaximo() : Integer.MAX_VALUE;
            if (vendidos + lc.getCantidad() > aforo) throw new SoldOutException("No hay suficiente aforo para el evento: " + evento.getTitulo());

            // Generar tickets ahora y asignar compradores si hay
            for (int i = 0; i < lc.getCantidad(); i++) {
                Ticket ticket = new Ticket();
                ticket.setCodigo(UUID.randomUUID().toString());
                ticket.setFechaCompra(LocalDateTime.now());
                ticket.setEvento(evento);
                ticket.setUsuario(compra.getUsuario());

                if (lc.getCompradores() != null && i < lc.getCompradores().size()) {
                    CompradorEmbeddable c = lc.getCompradores().get(i);
                    ticket.setCompradorNombre(c.getNombre());
                    ticket.setCompradorEmail(c.getEmail());
                    ticket.setCompradorDocumento(c.getDocumentoIdentidad());
                    ticket.setCompradorFechaNacimiento(c.getFechaNacimiento());
                } else if (compra.getCompradorNombre() != null) {
                    ticket.setCompradorNombre(compra.getCompradorNombre());
                    ticket.setCompradorEmail(compra.getCompradorEmail());
                }

                ticket.setCompra(compra);
                ticketRepo.save(ticket);
            }

            total = total.add(lc.getSubtotal());
        }

        compra.setTotal(total);
        compra.setEstado("PAID");
        compraRepo.save(compra);

        // Vaciar carrito si existe
        if (compra.getUsuario() != null) {
            carritoRepo.findByUsuarioId(compra.getUsuario().getId()).ifPresent(c -> {
                c.getLineas().clear();
                carritoRepo.save(c);
            });
        }

        return compra;
    }
}
