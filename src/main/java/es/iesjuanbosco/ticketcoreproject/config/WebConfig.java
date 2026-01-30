package es.iesjuanbosco.ticketcoreproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permitir CORS para todas las rutas, orígenes y métodos
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Primero intenta servir desde frontend/dist (útil en desarrollo si ejecutas `npm run build`)
        registry.addResourceHandler("/**")
                .addResourceLocations("file:frontend/dist/", "classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Fallback: any path not handled by controllers will be forwarded to index.html (SPA)
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}