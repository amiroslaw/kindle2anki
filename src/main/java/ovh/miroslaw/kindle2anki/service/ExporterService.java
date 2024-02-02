package ovh.miroslaw.kindle2anki.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.model.MWProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;

@Service
public class ExporterService {

    @Value("${dictionary.tsv.path}")
    private String dictionaryTsv;
    @Value("${vocab.tsv.path}")
    private String vocabTsv;

    public void exportVocabulary(List<String> vocab) {
        final String vocabTxt = String.join(System.lineSeparator(), vocab);
        writeToFile(vocabTxt, vocabTsv);
    }

    public void exportDictionary(List<Dictionary> dictionaries) {
        final String txt = dictionaries.stream()
                .distinct()
                .map(this::toDictionaryRow)
                .collect(Collectors.joining(System.lineSeparator()));
        writeToFile(txt, dictionaryTsv);
    }

    private void writeToFile(String txt, String fileName) {
        try {
            Files.writeString(Path.of(fileName), txt, StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            ANSI_PRINT.accept("Unable to write file " + fileName, AnsiColor.RED);
        }
    }

    private String toDictionaryRow(Dictionary dict) {
        return "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s".formatted(dict.getWord(),
                dict.getCategory(),
                getIllustration(dict.getIllustration()),
                dict.getPronunciations().getFirst(),
                getAudio(dict.getAudios()),
                dict.getTranslation(),
                dict.getDefinitions().getFirst(),
                dict.getExamples().getFirst()
        );
    }

    private String getIllustration(String illustration) {
        if (illustration.isBlank()) {
            return Strings.EMPTY;
        }
        return MWProperties.ART_URL.getValue() + illustration;
    }

    private String getAudio(List<String> dict) {
        if (dict.isEmpty()) {
            return Strings.EMPTY;
        }
        return dict.getFirst() + "." + MWProperties.AUDIO_EXTENSION.getValue();
    }
}
