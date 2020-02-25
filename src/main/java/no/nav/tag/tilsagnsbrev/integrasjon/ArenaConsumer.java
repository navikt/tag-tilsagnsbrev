package no.nav.tag.tilsagnsbrev.integrasjon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tilsagnsbrev.behandler.CidManager;
import no.nav.tag.tilsagnsbrev.behandler.TilsagnsbrevBehandler;
import no.nav.tag.tilsagnsbrev.dto.ArenaMelding;
import no.nav.tag.tilsagnsbrev.dto.tilsagnsbrev.TilsagnUnderBehandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Slf4j
@Component
@AllArgsConstructor
public class ArenaConsumer {

    public static final String topic = "aapen-tiltak-tilsagnsbrevGodkjent-v1";

    private CidManager cidManager;

    @Autowired
    private TilsagnsbrevBehandler tilsagnsbrevbehandler;

    @KafkaListener(topics = topic)
    public void lyttPaArenaTilsagn(ArenaMelding arenaMelding){
        final UUID cid = cidManager.opprettCorrelationId();
        log.debug("Ny melding hentet fra topic {}", arenaMelding);

        TilsagnUnderBehandling tilsagnUnderBehandling = TilsagnUnderBehandling.builder()
                .opprettet(LocalDateTime.now())
                .arenaMelding(arenaMelding)
                .cid(cid).build();
        try {
            tilsagnsbrevbehandler.behandleOgVerifisereTilsagn(tilsagnUnderBehandling);
        } finally {
            cidManager.fjernCorrelationId();
        }
    }
}
