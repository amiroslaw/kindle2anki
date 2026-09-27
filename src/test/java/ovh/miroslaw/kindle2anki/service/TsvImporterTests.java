package ovh.miroslaw.kindle2anki.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.dictionary.repository.DictionaryRepository;
import ovh.miroslaw.kindle2anki.model.Tsv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TsvImporterTests {

    @Mock
    private DictionaryRepository dictionaryRepository;
    @Mock
    private DictionaryProvider dictionaryProvider;
    @Mock
    private DictionaryMapper dictionaryMapper;
    @Mock
    private MWMediaDownloaderService downloaderService;
    @InjectMocks
    private TsvImporter importer;

    @Test
    void skipsExistingWordsMapsValidRowsAndDownloadsOnlySavedEntries(@TempDir Path directory) throws IOException {
        Path file = directory.resolve("vocab.tsv");
        Files.writeString(file, "known\tknown translation\nfresh\tnew translation\nunmapped\n");

        Dictionary known = dictionary("known", "known translation");
        Dictionary fresh = dictionary("fresh", "new translation");
        when(dictionaryRepository.findAll()).thenReturn(List.of(known));
        when(dictionaryProvider.getDefinition("fresh")).thenReturn(Optional.of("fresh-json"));
        when(dictionaryProvider.getDefinition("unmapped")).thenReturn(Optional.of("unmapped-json"));
        when(dictionaryMapper.map("fresh-json", new Tsv("fresh", "new translation")))
                .thenReturn(Optional.of(fresh));
        when(dictionaryMapper.map("unmapped-json", new Tsv("unmapped", ""))).thenReturn(Optional.empty());
        when(dictionaryRepository.saveAll(anyList())).thenReturn(List.of(fresh));

        importer.importTsv(file.toFile());

        verify(dictionaryProvider, never()).getDefinition("known");
        verify(dictionaryRepository).saveAll(argThat(dictionaries -> {
            var saved = dictionaries.iterator();
            return saved.hasNext() && saved.next().equals(fresh) && !saved.hasNext();
        }));
        verify(downloaderService).downloadMedia(fresh);
        verify(downloaderService, never()).downloadMedia(known);
    }

    @Test
    void convertRowToDictionaryReturnsEmptyWhenMapperCannotMapDefinition() {
        Tsv row = new Tsv("unknown");
        when(dictionaryProvider.getDefinition("unknown")).thenReturn(Optional.of("unknown-json"));
        when(dictionaryMapper.map("unknown-json", row)).thenReturn(Optional.empty());

        assertTrue(importer.convertRowToDictionary(row).isEmpty());
    }

    private static Dictionary dictionary(String word, String translation) {
        return new Dictionary(word, List.of("definition"), "noun", translation, List.of(), List.of(), List.of(), "");
    }
}
