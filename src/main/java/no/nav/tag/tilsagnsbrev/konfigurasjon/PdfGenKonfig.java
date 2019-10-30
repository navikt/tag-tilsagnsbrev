package no.nav.tag.tilsagnsbrev.konfigurasjon;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties(prefix = "tilsagnsbrev.integrasjon.pdfgen")
public class PdfGenKonfig {
    private String uri;
}
