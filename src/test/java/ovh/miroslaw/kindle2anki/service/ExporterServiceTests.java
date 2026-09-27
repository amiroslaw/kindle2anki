package ovh.miroslaw.kindle2anki.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExporterServiceTests {

    @Test
    void exportsDistinctDictionaryRowsWithAnkiMediaAndEmptyFieldFormatting(@TempDir Path directory)
            throws IOException {
        Path output = directory.resolve("dictionary.tsv");
        ExporterService exporter = new ExporterService();
        ReflectionTestUtils.setField(exporter, "dictionaryTsv", output.toString());

        Dictionary populated = new Dictionary("serendipity", List.of("a fortunate discovery"), "noun", "lucky find",
                List.of("səˈren.də.pə.tē"), List.of("serend01"), List.of("It was serendipity."), "image.gif");
        Dictionary emptyFields = new Dictionary("ephemeral", List.of(), "adjective", "short-lived",
                List.of(), List.of(), List.of(), "");

        exporter.exportDictionary(List.of(populated, populated, emptyFields));

        assertEquals(String.join(System.lineSeparator(),
                        "serendipity\tnoun\thttps://merriam-webster.com/assets/ld/images/legacy_print_images/image.gif"
                                + "\tsəˈren.də.pə.tē\t[sound:serend01.ogg]\tlucky find\ta fortunate discovery"
                                + "\tIt was serendipity.",
                        "ephemeral\tadjective\t\t\t\tshort-lived\t\t"),
                Files.readString(output));
    }
}
