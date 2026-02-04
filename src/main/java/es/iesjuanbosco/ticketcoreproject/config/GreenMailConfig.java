package es.iesjuanbosco.ticketcoreproject.config;

import com.icegreen.greenmail.spring.GreenMailBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "greenmail.enabled", havingValue = "true")
public class GreenMailConfig {

    @Bean
    public GreenMailBean greenMailBean() {
        GreenMailBean greenMailBean = new GreenMailBean();
        greenMailBean.setPortOffset(3000); // SMTP en puerto 3025 (25 + 3000)
        greenMailBean.setAutostart(true);
        greenMailBean.setSmtpProtocol(true);
        greenMailBean.setHostname("localhost");

        return greenMailBean;
    }
}

