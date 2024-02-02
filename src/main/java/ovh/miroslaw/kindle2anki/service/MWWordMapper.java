package ovh.miroslaw.kindle2anki.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.model.Tsv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;
import static ovh.miroslaw.kindle2anki.model.MWProperties.ART;
import static ovh.miroslaw.kindle2anki.model.MWProperties.AUDIO;
import static ovh.miroslaw.kindle2anki.model.MWProperties.CATEGORY;
import static ovh.miroslaw.kindle2anki.model.MWProperties.EXAMPLE_TEXT;
import static ovh.miroslaw.kindle2anki.model.MWProperties.PRONUNCIATIONS;
import static ovh.miroslaw.kindle2anki.model.MWProperties.SHORTDEF;
import static ovh.miroslaw.kindle2anki.service.MWMediaDownloaderService.MW_PICTURE_EXTENSION;

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
                        replaceTokens(data.findValuesAsText(EXAMPLE_TEXT.getValue())),
                        changeExtension(data.findPath(ART.getValue()).asText())
                ));
            }
        } catch (JsonProcessingException e) {
            ANSI_PRINT.accept("Unable to process json for " + tsv.word(), AnsiColor.RED);
        }
        return Optional.empty();
    }

    private List<String> replaceTokens(List<String> examples) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{sc}", Strings.EMPTY);
        replacements.put("{bc}", Strings.EMPTY);
        replacements.put("{inf}", "<sub>");
        replacements.put("{/inf}", "</sub>");
        replacements.put("{sup}", "<sup>");
        replacements.put("{/sup}", "</sup>");
        replacements.put("{b}", "<b>");
        replacements.put("{/b}", "</b>");
        replacements.put("{it}", "<i>");
        replacements.put("{/it}", "</i>");
        replacements.put("{ldquo}", "&ldquo;");
        replacements.put("{rdquo}", "&rdquo;");

        return examples.stream()
                .map(s -> replace(replacements, s))
                .toList();
    }

    private String replace(Map<String, String> replacements, String result) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private String changeExtension(String illustration) {
        if (illustration == null || illustration.isBlank()) {
            return Strings.EMPTY;
        }
        return illustration.substring(0, illustration.lastIndexOf('.')) + MW_PICTURE_EXTENSION;
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
