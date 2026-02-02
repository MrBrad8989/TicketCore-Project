package es.iesjuanbosco.ticketcoreproject.config;

import es.iesjuanbosco.ticketcoreproject.service.TicketmasterSyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@ConditionalOnProperty(name = "app.load-data", havingValue = "true", matchIfMissing = true)
public class DataLoader implements CommandLineRunner {

    private final TicketmasterSyncService ticketmasterService;
    private final DataSource dataSource;

    public DataLoader(TicketmasterSyncService ticketmasterService, DataSource dataSource) {
        this.ticketmasterService = ticketmasterService;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        System.out.println("--- DataLoader: comprobando y ejecutando script de actualización de precios si existe ---");

        // Buscar primero en classpath
        Resource resource = new ClassPathResource("actualizar_precios.sql");
        if (!resource.exists()) {
            // Intentar cargar desde filesystem (ruta relativa a la ejecución)
            resource = new FileSystemResource("actualizar_precios.sql");
        }

        if (resource.exists()) {
            System.out.println("Se ha encontrado 'actualizar_precios.sql' en: " + resource.getDescription());
            try (Connection conn = dataSource.getConnection()) {
                // Ejecuta el script SQL de forma segura
                ScriptUtils.executeSqlScript(conn, resource);
                System.out.println("Script 'actualizar_precios.sql' ejecutado correctamente.");
            } catch (Exception e) {
                System.err.println("Error ejecutando 'actualizar_precios.sql': " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró 'actualizar_precios.sql' en classpath ni en filesystem; saltando actualización por script.");
        }
    }
}