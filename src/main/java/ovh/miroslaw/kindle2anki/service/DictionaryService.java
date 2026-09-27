package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.shell.jline.tui.table.ArrayTableModel;
import org.springframework.shell.jline.tui.table.BorderStyle;
import org.springframework.shell.jline.tui.table.TableBuilder;
import org.springframework.shell.jline.tui.table.TableModel;
import org.springframework.stereotype.Service;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.dictionary.repository.DictionaryRepository;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    @Value("${vocab.tsv.path}") String vocabTsv;
    private final TsvImporter tsvImporter;
    private final MWMediaDownloaderService downloaderService;
    private final DictionaryRepository dictionaryRepository;

    /**
     * Retrieve the list of dictionaries form database.
     *
     * @return the list of dictionaries
     */
    public List<Dictionary> getDictionary() {
        return dictionaryRepository.findAll();
    }

    public void importTsv() {
        tsvImporter.importTsv(new File(vocabTsv));
    }

    public void importTsv(File tsvFile) {
        if (tsvFile.exists()) {
            tsvImporter.importTsv(tsvFile);
        } else {
            ANSI.apply("File not found: " + tsvFile.getAbsolutePath(), AnsiColor.RED);
        }
    }

    public String addDictionary(String searchWord) {
        return tsvImporter.addDictionary(searchWord)
                .map(dictionary -> {
                    downloaderService.downloadMedia(dictionary);
                    return dictionaryRepository.save(dictionary);
                })
                .map(Dictionary::print)
                .orElseGet(() -> ANSI.apply("Unable to find definition for " + searchWord, AnsiColor.RED));
    }

    public void showDictionary() {
        final List<Object[]> data = getDictionary().stream()
                .map(d -> new Object[]{d.getWord(), d.getFirstPronunciation(), d.getFirstDefinition(),
                        d.getFirstExample(), d.getCategory()})
                .collect(Collectors.toList());
        data.addFirst(new Object[]{"Word", "Pronunciation", "Definition", "Example", "Category"});

        TableModel model = new ArrayTableModel(data.toArray(Object[][]::new));
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        System.out.printf("%n%s%n", tableBuilder.build().render(110));
    }
}
