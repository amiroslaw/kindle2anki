package ovh.miroslaw.kindle2anki.service;

import org.junit.jupiter.api.Test;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.model.Tsv;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MWDictionaryMapperTests {

    private final MWDictionaryMapper mapper = new MWDictionaryMapper();

    @Test
    void mapsDefinitionMediaAndMarkupFromTheFirstResult() {
        String json = """
                [
                  {
                    "fl": "noun",
                    "shortdef": ["the first definition", "another definition"],
                    "hwi": {
                      "prs": [
                        {"ipa": "səˈren.də.pə.tē", "sound": {"audio": "serend01"}},
                        {"ipa": "alternate", "sound": {"audio": "serend02"}}
                      ]
                    },
                    "def": [
                      {
                        "t": "A {b}fortunate{/b} {inf}discovery{/inf} & {ldquo}luck{rdquo}.",
                        "sseq": [[{"dt": [["text", "definition text"]]}]]
                      }
                    ],
                    "art": {"artid": "serendipity.jpg"}
                  },
                  {"fl": "verb", "shortdef": ["a later result"]}
                ]
                """;

        Dictionary dictionary = mapper.map(json, new Tsv("serendipity", "finding something valuable"))
                .orElseThrow();

        assertEquals("serendipity", dictionary.getWord());
        assertEquals("noun", dictionary.getCategory());
        assertEquals("finding something valuable", dictionary.getTranslation());
        assertEquals(List.of("the first definition", "another definition"), dictionary.getDefinitions());
        assertEquals(List.of("səˈren.də.pə.tē", "alternate"), dictionary.getPronunciations());
        assertEquals(List.of("serend01", "serend02"), dictionary.getAudios());
        assertEquals(List.of("A <b>fortunate</b> <sub>discovery</sub> & &ldquo;luck&rdquo;."),
                dictionary.getExamples());
        assertEquals("serendipity.gif", dictionary.getIllustration());
    }

    @Test
    void returnsEmptyWhenTheResponseHasNoShortDefinitionsOrIsMalformed() {
        assertTrue(mapper.map("[{\"fl\":\"noun\"}]", new Tsv("unknown")).isEmpty());
        assertTrue(mapper.map("not-json", new Tsv("unknown")).isEmpty());
    }
}
