package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.kindle2anki.TerminalUtil;
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
import java.util.stream.Gatherers;

@Service
@RequiredArgsConstructor
public class TsvImporter {

    private static final int MAX_CONCURRENT_DOWNLOADS = 8;
    private final DictionaryRepository dictionaryRepository;
    private final DictionaryProvider dictionaryProvider;
    private final DictionaryMapper dictionaryMapper;
    private final MWMediaDownloaderService downloaderService;

    /**
     * Imports the data from a TSV file, converts the data to dictionaries, and saves them.
     *
     * @param tsvFile the TSV file to import
     */
    public void importTsv(File tsvFile) {
        final List<Dictionary> dictionaries = removeDuplicatesFromDB(tsvFile).stream()
                .gather(Gatherers.mapConcurrent(MAX_CONCURRENT_DOWNLOADS, this::convertRowToDictionary))
                .flatMap(Optional::stream).collect(Collectors.toList());

        save(dictionaries);
    }

    Optional<Dictionary> convertRowToDictionary(Tsv tsv) {
        return dictionaryProvider.getDefinition(tsv.word())
                .flatMap(json -> dictionaryMapper.map(json, tsv))
                .or(() -> {
                    TerminalUtil.ANSI_PRINT.accept("No definition found for word: " + tsv.word(), AnsiColor.YELLOW);
                    return Optional.empty();
                });
    }

    Optional<Dictionary> addDictionary(String searchWord) {
        return this.convertRowToDictionary(new Tsv(searchWord, ""));
    }

    private List<Tsv> removeDuplicatesFromDB(File tsvFile) {
        final List<Tsv> existingWords = getWordsFromDB();
        List<Tsv> tsvs = readTsv(tsvFile);
        tsvs.removeAll(existingWords);
        return tsvs;
    }

    private void save(List<Dictionary> dictionaries) {
        dictionaryRepository.saveAll(dictionaries).stream()
                .gather(Gatherers.mapConcurrent(MAX_CONCURRENT_DOWNLOADS, dictionary -> {
                    downloaderService.downloadMedia(dictionary);
                    return dictionary;
                })).toList();
    }

    private List<Tsv> readTsv(File tsvFile) {
        try {
            return Files.readAllLines(tsvFile.toPath()).parallelStream().map(Tsv::lineToObject).flatMap(Optional::stream).collect(Collectors.toList());
        } catch (IOException _) {
            TerminalUtil.ANSI_PRINT.accept("Unable to read file " + tsvFile.getName(), AnsiColor.RED);
            return Collections.emptyList();
        }
    }

    private List<Tsv> getWordsFromDB() {
        return dictionaryRepository.findAll().parallelStream().map(Tsv::fromDictionary).distinct().toList();
    }
}
