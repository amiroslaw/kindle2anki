package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.dictionary.repository.DictionaryRepository;
import ovh.miroslaw.kindle2anki.model.Tsv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI;
import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    @Value("${vocab.tsv.path}")
    private String vocabTsv;
    private final DictionaryRepository dictionaryRepository;
    private final DictionaryProvider dictionaryProvider;
    private final WordMapper wordMapper;
    private final MWMediaDownloaderService downloaderService;

    public List<Dictionary> getDictionary() {
        return dictionaryRepository.findAll();
    }

    public void importTsv() {
        importTsv(new File(vocabTsv));
    }

    public void importTsv(File tsvFile) {
        final List<Tsv> existingWords = getWords();
        List<Tsv> tsvs = readTsv(tsvFile);
        tsvs.removeAll(existingWords);
        final List<Dictionary> dictionaries = tsvs.parallelStream()
                .map(this::convertRowToDictionary)
                .flatMap(Optional::stream)
                .toList();

        save(dictionaries);
    }

    public String addDictionary(String searchWord) {
        return convertRowToDictionary(new Tsv(searchWord))
                .map(dictionary -> {
                    downloaderService.downloadMedia(dictionary);
                    return dictionaryRepository.save(dictionary);
                })
                .map(Dictionary::print)
                .orElseGet(() -> ANSI.apply("Unable to find definition for " + searchWord, AnsiColor.RED));
    }

    private Optional<Dictionary> convertRowToDictionary(Tsv tsv) {
        final String json = dictionaryProvider.getDefinition(tsv.word());
        return wordMapper.map(json, tsv);
    }

    private void save(List<Dictionary> dictionaries) {
        dictionaryRepository.saveAll(dictionaries)
                .parallelStream()
                .forEach(downloaderService::downloadMedia);
    }

    private List<Tsv> readTsv(File tsvFile) {
        try {
            return Files.readAllLines(tsvFile.toPath()).parallelStream()
                    .map(Tsv::lineToObject)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            ANSI_PRINT.accept("Unable to read file " + tsvFile.getName(), AnsiColor.RED);
            return Collections.emptyList();
        }
    }

    public List<Tsv> getWords() {
        return dictionaryRepository.findAll().parallelStream()
                .map(Tsv::fromDictionary)
                .distinct()
                .toList();
    }
}
