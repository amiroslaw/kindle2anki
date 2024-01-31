package ovh.miroslaw.kindle2anki;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ovh.miroslaw.kindle2anki.model.Word;
import ovh.miroslaw.kindle2anki.service.DictionaryProvider;
import ovh.miroslaw.kindle2anki.service.DictionaryService;
import ovh.miroslaw.kindle2anki.service.ExporterService;
import ovh.miroslaw.kindle2anki.service.MediaDownloaderService;
import ovh.miroslaw.kindle2anki.service.VocabularyService;
import ovh.miroslaw.kindle2anki.service.WordMapper;

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

    @Command(description = "Get word definition", alias = "d")
    public String definition(@Option(shortNames = 's', required = true) String searchWord) {
        String json = mwClient.getDefinition(searchWord);
        Optional<Word> word = mapper.map(json);
        word.ifPresent(downloaderService::downloadMedia);
        return searchWord;
    }

    // for what?
    @Command(description = "Convert words from kindle database", alias = "k")
    public List<String> database() {
        return vocabularyService.getVocabulary();
    }

    @Command(description = "Convert words from kindle database", alias = "v")
    public void exportVocabulary() {
        exporter.exportVocabulary(vocabularyService.getVocabulary());
    }

    @Command(description = "Export dictionary to TSV for anki", alias = "a")
    public void exportDictionary() {
        exporter.exportDictionary(dictionaryService.getDictionary());
    }
}
