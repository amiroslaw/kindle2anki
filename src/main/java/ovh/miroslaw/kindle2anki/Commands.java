package ovh.miroslaw.kindle2anki;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.model.Tsv;
import ovh.miroslaw.kindle2anki.service.DictionaryProvider;
import ovh.miroslaw.kindle2anki.service.DictionaryService;
import ovh.miroslaw.kindle2anki.service.ExporterService;
import ovh.miroslaw.kindle2anki.service.MediaDownloaderService;
import ovh.miroslaw.kindle2anki.service.VocabularyService;
import ovh.miroslaw.kindle2anki.service.WordMapper;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Command
@RequiredArgsConstructor
public class Commands {

    private final DictionaryProvider mwClient;
    private final WordMapper mapper;
    private final VocabularyService vocabularyService;
    private final DictionaryService dictionaryService;
    private final MediaDownloaderService downloaderService;
    private final ExporterService exporter;

    @Command(description = "Get a word definition", alias = "w")
    public String definition(@Option(shortNames = 's', required = true) String searchWord) {
        String json = mwClient.getDefinition(searchWord);
        Optional<Dictionary> word = mapper.map(json, new Tsv(searchWord, ""));
//        word.ifPresent(downloaderService::downloadMedia);
        return searchWord;
    }

    @Command(description = "Convert words from kindle database to anki", alias = "c")
    public void kindleToAnki() {
        exportVocabulary(null);
        dictionaryService.importTsv();
        exportDictionary();
    }

    @Command(description = "Import words from a TSV file, fetch information from dictionary and save to the database",
             alias = "i")
    public void importTsv() {
        dictionaryService.importTsv();
    }

    @Command(description = "Import words from a TSV file, fetch information from dictionary and save to the database",
             alias = "i")
    public void importTsv(@Option(longNames = "tsv", shortNames = 't', label = "file",
                                  description = "TSV file with words") File tsv) {
        dictionaryService.importTsv(tsv);
    }

    @Command(description = "Export kindle vocabulary to a TSV file", alias = "v")
    public void exportVocabulary(@Option(longNames = "From", shortNames = 'f', label = "date",
                                         description = "Date from which to export words. Format: yyyy-MM-dd (2022-01-31)")
            Optional<String> dateFrom) {
        final List<String> vocab = dateFrom.map(vocabularyService::getVocabulary)
                .orElseGet(vocabularyService::getVocabulary);
        exporter.exportVocabulary(vocab);
    }

    @Command(description = "Export dictionary to a TSV file for anki", alias = "d")
    public void exportDictionary() {
        exporter.exportDictionary(dictionaryService.getDictionary());
    }
}
