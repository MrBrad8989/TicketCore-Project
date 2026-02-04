package es.iesjuanbosco.ticketcoreproject.config;

import com.icegreen.greenmail.spring.GreenMailBean;
import com.icegreen.greenmail.util.GreenMailUtil;
import jakarta.mail.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Listener que muestra en consola los correos enviados al servidor GreenMail local
 */
@Component
@EnableAsync
@ConditionalOnProperty(value = "greenmail.enabled", havingValue = "true")
public class GreenMailListener {

    private static final Logger logger = LoggerFactory.getLogger(GreenMailListener.class);

    @Autowired(required = false)
    private GreenMailBean greenMailBean;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (greenMailBean != null) {
            logger.info("🟢 GreenMail SMTP Server iniciado en localhost:3025");
            logger.info("📬 Los correos enviados se mostrarán en la consola");
            startEmailMonitoring();
        }
    }

    @Async
    public void startEmailMonitoring() {
        new Thread(() -> {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(2);
                    if (greenMailBean != null && greenMailBean.getGreenMail() != null) {
                        Message[] messages = greenMailBean.getGreenMail().getReceivedMessages();
                        if (messages != null && messages.length > 0) {
                            for (Message msg : messages) {
                                logger.info("\n" +
                                    "╔═══════════════════════════════════════════════════════════╗\n" +
                                    "║           📧 CORREO ENVIADO (GreenMail Local)           ║\n" +
                                    "╠═══════════════════════════════════════════════════════════╣\n" +
                                    "║ Para: {}\n" +
                                    "║ Asunto: {}\n" +
                                    "║ Contenido:\n{}\n" +
                                    "╚═══════════════════════════════════════════════════════════╝",
                                    msg.getAllRecipients()[0],
                                    msg.getSubject(),
                                    GreenMailUtil.getBody(msg).substring(0, Math.min(200, GreenMailUtil.getBody(msg).length())) + "..."
                                );
                            }
                            // Limpiar mensajes procesados
                            greenMailBean.getGreenMail().purgeEmailFromAllMailboxes();
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error monitorizando correos: {}", e.getMessage());
            }
        }).start();
    }
}

