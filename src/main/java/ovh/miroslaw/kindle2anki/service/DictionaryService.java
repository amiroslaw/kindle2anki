package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.dictionary.repository.DictionaryRepository;
import ovh.miroslaw.kindle2anki.model.Tsv;

import java.io.File;
import java.util.List;

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
        return tsvImporter.convertRowToDictionary(new Tsv(searchWord))
                .map(dictionary -> {
                    downloaderService.downloadMedia(dictionary);
                    return dictionaryRepository.save(dictionary);
                })
                .map(Dictionary::print)
                .orElseGet(() -> ANSI.apply("Unable to find definition for " + searchWord, AnsiColor.RED));
    }
}




