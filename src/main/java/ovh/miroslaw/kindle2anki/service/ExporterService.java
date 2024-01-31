package ovh.miroslaw.kindle2anki.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExporterService {

    @Value("${config.path}")
    private String configPath;
    private static final String VOCAB_TSV = "/vocab.tvs";
    private static final String DICTIONARY_TSV = "/dictionary.tvs";

    public void exportVocabulary(List<String> vocab) {
        final String vocabTxt = String.join(System.lineSeparator(), vocab);
        writeToFile(vocabTxt, VOCAB_TSV);
    }

    public void exportDictionary(List<Dictionary> dictionaries) {
        final String txt = dictionaries.stream()
                .distinct()
                .map(this::toDictionaryRow)
                .collect(Collectors.joining(System.lineSeparator()));
        writeToFile(txt, DICTIONARY_TSV);
    }

    private void writeToFile(String txt, String fileName) {
        try {
            Files.writeString(Path.of(configPath + fileName), txt, StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toDictionaryRow(Dictionary dict) {
        return "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s".formatted(dict.getWord(),
                dict.getDefinitions().getFirst(),
                dict.getCategory(),
                dict.getCategory(),
                dict.getAudios().getFirst(),
                getExample(dict.getExamples()),
                dict.getIllustration(),
                dict.getPronunciations().getFirst()
        );
    }

    private String getExample(List<String> examples) {
        return examples.stream()
                .min(Comparator.comparingInt(String::length))
                .orElseThrow();
    }
}
