package ovh.miroslaw.kindle2anki;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ovh.miroslaw.kindle2anki.service.DictionaryService;
import ovh.miroslaw.kindle2anki.service.ExporterService;
import ovh.miroslaw.kindle2anki.service.VocabularyService;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;

@Command
@RequiredArgsConstructor
public class Commands {

    private final VocabularyService vocabularyService;
    private final DictionaryService dictionaryService;
    private final ExporterService exporter;

    @Command(description = "Get a word definition", alias = "w")
    public String definition(@Option(shortNames = 's', required = true) String searchWord) {
        return dictionaryService.addDictionary(searchWord);
    }

    @Command(description = "Convert words from kindle database to anki", alias = "c")
    public void kindleToAnki() {
        exportVocabulary(Optional.empty(), false);
        dictionaryService.importTsv();
        exportDictionary();
    }

    @Command(description = "Import words from a TSV file, fetch information from dictionary and save to the database. It will import vocabulary from the 'vocab.tsv' file  from the configuration folder.",
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

    @Command(
            description = "Export kindle vocabulary to a TSV file. By default it will only export vocabulary from the last export.",
            alias = "v")
    public void exportVocabulary(
            @Option(description = "Date from which to export words. Format: yyyy-MM-dd (2022-01-31)",
                    longNames = "from", shortNames = 'f') Optional<String> dateFrom,
            @Option(longNames = "all", shortNames = 'a',
                    description = "Export all vocabulary. Will omit `from` argument.") boolean all) {
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

    @Command(description = "Export dictionary to a TSV file for anki", alias = "d")
    public void exportDictionary() {
        exporter.exportDictionary(dictionaryService.getDictionary());
    }
}
