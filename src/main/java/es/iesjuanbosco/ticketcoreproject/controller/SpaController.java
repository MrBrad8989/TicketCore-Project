package es.iesjuanbosco.ticketcoreproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.MediaType;

/**
 * Simple controller to forward non-API requests to index.html so the SPA can handle routing.
 * This assumes the static frontend build is in src/main/resources/static (copied there by the build).
 */
@Controller
public class SpaController {

    // Forward root
    @RequestMapping(value = "/")
    public String index() {
        return "forward:/index.html";
    }

    // Forward any path that does not contain a dot (to avoid forwarding requests for files like *.js, *.css)
    @RequestMapping(value = "{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }

    // Captura rutas sin punto (sin extensión) y las redirige a index.html
    @GetMapping(value = {"/{path:[^.]*}", "/**/{path:[^.]*}"}, produces = MediaType.TEXT_HTML_VALUE)
    public String forward(HttpServletRequest request) {
        // Si la ruta empieza por /api, no hacemos forward (dejar que otros controladores la manejen)
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/") || uri.equals("/api")) {
            return "forward:/error"; // permitirá que /api handlers gestionen o devuelvan 404 apropiado
        }
        return "forward:/index.html";
    }
}
