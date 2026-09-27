package ovh.miroslaw.kindle2anki.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ovh.miroslaw.kindle2anki.dictionary.model.LastExport;
import ovh.miroslaw.kindle2anki.dictionary.repository.LastExportRepository;
import ovh.miroslaw.kindle2anki.vocabulary.repository.VocabularyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VocabularyServiceTests {

    @Mock
    private VocabularyRepository vocabularyRepository;
    @Mock
    private LastExportRepository lastExportRepository;
    @InjectMocks
    private VocabularyService vocabularyService;

    @Test
    void dateExportIncludesWordsAtOrAfterUtcMidnightThatWereNotSeenBefore() {
        when(vocabularyRepository.findDistinctOrderedAllWordsByTimestampLessThan(1_704_153_600_000L))
                .thenReturn(List.of("already-known"));
        when(vocabularyRepository.findDistinctOrderedAllWordsByTimestampGreaterThanEqual(1_704_153_600_000L))
                .thenReturn(new ArrayList<>(List.of("already-known", "new-word")));

        assertEquals(List.of("new-word"), vocabularyService.getVocabulary("2024-01-02"));

        verify(vocabularyRepository).findDistinctOrderedAllWordsByTimestampLessThan(1_704_153_600_000L);
        verify(vocabularyRepository).findDistinctOrderedAllWordsByTimestampGreaterThanEqual(1_704_153_600_000L);
        verify(lastExportRepository).save(any(LastExport.class));
    }

    @Test
    void invalidDateReturnsNoWordsWithoutQueryingOrUpdatingExportState() {
        assertTrue(vocabularyService.getVocabulary("2024-02-30").isEmpty());
        verifyNoInteractions(vocabularyRepository, lastExportRepository);
    }

    @Test
    void recentExportUsesTheSavedTimestampAndRecordsTheNewExportTime() {
        when(lastExportRepository.findLastTimestamp()).thenReturn(Optional.of(1_000L));
        when(vocabularyRepository.findDistinctOrderedAllWordsByTimestampLessThan(1_000L))
                .thenReturn(List.of());
        when(vocabularyRepository.findDistinctOrderedAllWordsByTimestampGreaterThanEqual(1_000L))
                .thenReturn(new ArrayList<>(List.of("new-word")));
        long before = System.currentTimeMillis();

        assertEquals(List.of("new-word"), vocabularyService.getRecentVocabulary());

        ArgumentCaptor<LastExport> exportCaptor = ArgumentCaptor.forClass(LastExport.class);
        verify(lastExportRepository).save(exportCaptor.capture());
        assertTrue(exportCaptor.getValue().getTimestamp() >= before);
    }

    @Test
    void recentExportWithoutHistoryReturnsAllVocabulary() {
        when(lastExportRepository.findLastTimestamp()).thenReturn(Optional.empty());
        when(vocabularyRepository.findDistinctOrderedAllWords()).thenReturn(List.of("alpha", "beta"));

        assertEquals(List.of("alpha", "beta"), vocabularyService.getRecentVocabulary());
        verify(vocabularyRepository).findDistinctOrderedAllWords();
        verify(lastExportRepository).save(any(LastExport.class));
    }
}
