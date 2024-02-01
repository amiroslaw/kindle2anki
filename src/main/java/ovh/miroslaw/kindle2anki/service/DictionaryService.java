package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.dictionary.repository.DictionaryRepository;
import ovh.miroslaw.kindle2anki.model.Tsv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    @Value("${config.path}")
    private String configPath;
    private static final String VOCAB_TSV = "/vocab.tvs";
    private final DictionaryRepository dictionaryRepository;
    private final DictionaryProvider mwClient;
    private final WordMapper wordMapper;
    private final MWMediaDownloaderService downloaderService;

    @Transactional
    public Dictionary save(Dictionary dictionary) {
        System.out.println("save " + dictionary.getWord());
        return dictionaryRepository.save(dictionary);
    }

    public List<Dictionary> getDictionary() {
        return dictionaryRepository.findAll();
    }

    public void importTsv() {
        importTsv(new File(configPath + VOCAB_TSV));
    }
    public void importTsv(File tsvFile) {
        List<Tsv> tsvs = new ArrayList<>();
        try {
//            tsvs = Files.readAllLines(tsvFile.toPath()).parallelStream()
            tsvs = Files.readAllLines(tsvFile.toPath()).stream()
                    .map(Tsv::lineToObject)
                    .flatMap(Optional::stream)
                    .toList();
        } catch (IOException e) {
            ANSI_PRINT.accept("Unable to read file " + tsvFile.getName(), AnsiColor.RED);
        }
        for (var tsv : tsvs) {
            final String json = mwClient.getDefinition(tsv.word());
            // better save to db in batch
            wordMapper.map(json, tsv)
                    .map(this::save)
                    .ifPresent(downloaderService::downloadMedia);
        }
    }
}
