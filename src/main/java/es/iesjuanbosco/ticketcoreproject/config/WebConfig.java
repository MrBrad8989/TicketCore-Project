package es.iesjuanbosco.ticketcoreproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permitir CORS para todas las rutas, orígenes y métodos
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}