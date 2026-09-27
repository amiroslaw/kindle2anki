package ovh.miroslaw.kindle2anki.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TsvTests {

    @Test
    void lineToObjectUsesTheFirstTwoColumnsAndIgnoresTheRest() {
        assertEquals(new Tsv("serendipity", "finding something valuable"),
                Tsv.lineToObject("serendipity\tfinding something valuable\textra").orElseThrow());
    }

    @Test
    void lineToObjectDefaultsTranslationWhenOnlyAWordIsPresent() {
        assertEquals(new Tsv("serendipity", ""), Tsv.lineToObject("serendipity").orElseThrow());
    }
}
