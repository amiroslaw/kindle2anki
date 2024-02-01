package ovh.miroslaw.kindle2anki.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.model.Tsv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;
import static ovh.miroslaw.kindle2anki.model.MWProperties.ART;
import static ovh.miroslaw.kindle2anki.model.MWProperties.AUDIO;
import static ovh.miroslaw.kindle2anki.model.MWProperties.CATEGORY;
import static ovh.miroslaw.kindle2anki.model.MWProperties.EXAMPLE_TEXT;
import static ovh.miroslaw.kindle2anki.model.MWProperties.PRONUNCIATIONS;
import static ovh.miroslaw.kindle2anki.model.MWProperties.SHORTDEF;

@Service
public class MWWordMapper implements WordMapper {

    @Override
    public Optional<Dictionary> map(String json, Tsv tsv) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // fetch only the first part of speech
            JsonNode data = mapper.readTree(json).get(0);
//            System.out.println(data);

            final JsonNode definitions = data.findPath(SHORTDEF.getValue());
            if (!definitions.isMissingNode()) {
                return Optional.of(new Dictionary(
                        tsv.word(),
                        nodeToList(definitions),
                        data.findValue(CATEGORY.getValue()).asText(),
                        tsv.translation(),
                        data.findValuesAsText(PRONUNCIATIONS.getValue()),
                        data.findValuesAsText(AUDIO.getValue()),
                        data.findValuesAsText(EXAMPLE_TEXT.getValue()),
                        data.findPath(ART.getValue()).asText()
                ));
            }
        } catch (JsonProcessingException e) {
            ANSI_PRINT.accept("Unable to process json for " + tsv.word(), AnsiColor.RED);
        }
        return Optional.empty();
    }

    private List<String> nodeToList(JsonNode values) {
        if (values == null) {
            return Collections.emptyList();
        }
        if (!values.isArray()) {
            throw new IllegalArgumentException();
        }
        List<String> texts = new ArrayList<>();
        for (JsonNode value : values) {
            texts.add(value.asText());
        }
        return texts;
    }
}
