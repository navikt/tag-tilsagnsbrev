package no.nav.tag.tilsagnsbrev.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tilsagnsbrev.behandler.TilsagnLoggRepository;
import no.nav.tag.tilsagnsbrev.dto.tilsagnsbrev.Tilsagn;
import no.nav.tag.tilsagnsbrev.dto.tilsagnsbrev.TilsagnUnderBehandling;
import no.nav.tag.tilsagnsbrev.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class TilsagnJsonMapper {

    @Autowired
    private TilsagnLoggRepository tilsagnLoggRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String JSON_ELEM_TILSAGN = "TILSAGN_DATA";
    private static final String JSON_ELEM_TILSAGNSBREV_ID = "TILSAGNSBREV_ID";

    private static final String OLD_PATTERN_3 = "}\"";
    private static final String NEW_PATTERN_3 = "}";

    public String opprettPdfJson(TilsagnUnderBehandling tilsagnUnderBehandling) {
        try {
            return objectMapper.writeValueAsString(tilsagnUnderBehandling.getTilsagn());
        } catch (Exception e) {
            log.error("Feil v/mapping fra Tilsagn-dto til pdf-tilsagn ", e);
            throw new DataException(e.getMessage());
        }
    }

    public void pakkUtArenaMelding(TilsagnUnderBehandling tilsagnUnderBehandling)  {
        try {
            ObjectNode after = tilsagnUnderBehandling.getArenaMelding().getAfter();
            tilsagnUnderBehandling.setTilsagnsbrevId(after.get(JSON_ELEM_TILSAGNSBREV_ID).intValue());
            log.info("Behandler melding med tilsagnsbrev-id {}", tilsagnUnderBehandling.getTilsagnsbrevId());
            String jsonStr = meldingtilJsonString(after.get(JSON_ELEM_TILSAGN).asText());
            tilsagnUnderBehandling.setJson(jsonStr);
        }catch (Exception e){
            log.error("Feil ved utpakking av Arena-melding til tilsagnsbrev {}", tilsagnUnderBehandling.getArenaMelding(), e);
            throw new DataException(e.getMessage());
        }
    }

     public void opprettTilsagn(TilsagnUnderBehandling tilsagnUnderBehandling)  {
         JsonNode tilsagnElem;
         Tilsagn tilsagn;
         try {
             tilsagnElem = objectMapper.readTree(tilsagnUnderBehandling.getJson());
             tilsagn = objectMapper.treeToValue(tilsagnElem, Tilsagn.class);
         } catch (Exception e) {
             log.error("Feil ved oppretting av Tilsagnsbrev fra Arenameldind Json", e);
             throw  new DataException(e.getMessage());
         }

        tilsagnUnderBehandling.setTilsagn(tilsagn);
        tilsagnUnderBehandling.setMappetFraArena(true);
    }

    private String meldingtilJsonString(String melding) {
        return StringUtils.replace(melding, OLD_PATTERN_3, NEW_PATTERN_3);
    }
}