package es.iesjuanbosco.ticketcoreproject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    /**
     * Envía un correo con asunto, texto HTML y un adjunto en memoria.
     * @param to Dirección de correo destino
     * @param subject Asunto del correo
     * @param htmlContent Contenido HTML del correo
     * @param attachmentBytes Bytes del archivo adjunto (PDF, ZIP, etc.)
     * @param attachmentName Nombre del archivo adjunto
     */
    public void sendEmailWithAttachment(String to, String subject, String htmlContent, byte[] attachmentBytes, String attachmentName) {
        if (mailSender == null) {
            logger.error("❌ JavaMailSender no configurado. Configure spring.mail.* en application.properties");
            throw new IllegalStateException("JavaMailSender no configurado. Configure spring.mail.* en application.properties para habilitar envíos de correo.");
        }

        try {
            logger.info("📧 Enviando correo a: {} | Asunto: {}", to, subject);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@ticketcore.local");
            helper.setText(htmlContent, true); // true = HTML

            if (attachmentBytes != null && attachmentName != null) {
                InputStreamSource attachment = new ByteArrayResource(attachmentBytes);
                helper.addAttachment(attachmentName, attachment);
                logger.info("📎 Adjunto añadido: {} ({} bytes)", attachmentName, attachmentBytes.length);
            }

            mailSender.send(message);
            logger.info("✅ Correo enviado exitosamente a: {}", to);

        } catch (Exception e) {
            logger.error("❌ Error al enviar correo a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error enviando email a " + to + ": " + e.getMessage(), e);
        }
    }
}
