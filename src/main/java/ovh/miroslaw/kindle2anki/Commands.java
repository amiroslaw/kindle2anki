package ovh.miroslaw.kindle2anki;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;
import ovh.miroslaw.kindle2anki.service.DictionaryService;
import ovh.miroslaw.kindle2anki.service.ExporterService;
import ovh.miroslaw.kindle2anki.service.VocabularyService;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;

@Component
@RequiredArgsConstructor
public class Commands {

    private final VocabularyService vocabularyService;
    private final DictionaryService dictionaryService;
    private final ExporterService exporter;

    @Command(description = "Get a word definition", alias = "w")
    public String definition(@Option(shortName = 's', required = true) String searchWord) {
        return dictionaryService.addDictionary(searchWord);
    }

    @Command(description = "Convert words from kindle database to Anki", alias = "c")
    public void kindleToAnki() {
        exportVocabulary(Optional.empty(), false);
        dictionaryService.importTsv();
        exportDictionary();
    }

    @Command(description = "Import words from a TSV file, fetch information from dictionary and save to the database. By default it will import vocabulary from the 'vocab.tsv' file  from the configuration folder.",
             alias = "i")
    public void importTsv(@Option(longName = "import-tsv", shortName = 't', description = "TSV file with words") File tsv) {
        if (tsv == null) {
            dictionaryService.importTsv();
        } else {
            dictionaryService.importTsv(tsv);
        }
    }

    @Command(description = "Export kindle vocabulary to a TSV file. By default it will only export vocabulary from the last export.", alias = "v")
    public void exportVocabulary(
            @Option(description = "Date from which to export words. Format: yyyy-MM-dd (2022-01-31)", longName = "from", shortName = 'f') Optional<String> dateFrom,
            @Option(longName = "all", shortName = 'a', description = "Export all vocabulary. Will omit `from` argument.") boolean all) {
        if (all && dateFrom.isPresent()) {
            ANSI_PRINT.accept("Cannot use `from` and `all` at the same time.", AnsiColor.RED);
            return;
        }

        if (all) {
            exporter.exportVocabulary(vocabularyService.getVocabulary());
        } else {
            final List<String> vocab = dateFrom.map(vocabularyService::getVocabulary)
                    .orElseGet(vocabularyService::getRecentVocabulary);
            exporter.exportVocabulary(vocab);
        }
    }

    @Command(description = "Export dictionary to a TSV file for Anki", alias = "d")
    public void exportDictionary() {
        exporter.exportDictionary(dictionaryService.getDictionary());
    }

    @Command(description = "List dictionary entries", alias = "l")
    public void dictionary() {
        dictionaryService.showDictionary();
    }
}
