package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ovh.miroslaw.kindle2anki.dictionary.model.LastExport;
import ovh.miroslaw.kindle2anki.dictionary.repository.LastExportRepository;
import ovh.miroslaw.kindle2anki.vocabulary.repository.VocabularyRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final LastExportRepository lastExportRepository;

    public List<String> getVocabulary(String dateFrom) {
        final long timestamp;
        try {
            timestamp = parseDate(dateFrom).toEpochMilli();
        } catch (DateTimeParseException e) {
            ANSI_PRINT.accept("Wrong date format. Format: yyyy-MM-dd (2022-01-31)", AnsiColor.RED);
            return Collections.emptyList();
        }
        return getVocabulary(timestamp);
    }

    public List<String> getVocabulary() {
        update();
        return vocabularyRepository.findDistinctOrderedAllWords();
    }

    public List<String> getRecentVocabulary() {
        return lastExportRepository.findLastTimestamp()
                .map(this::getVocabulary)
                .orElseGet(this::getVocabulary);
    }

    private static Instant parseDate(String dateFrom) {
        return LocalDate.parse(dateFrom).atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private List<String> getVocabulary(long timestamp) {
        final List<String> olderWords = vocabularyRepository.findDistinctOrderedAllWordsByTimestampLessThan(timestamp);
        List<String> newerWords = vocabularyRepository.findDistinctOrderedAllWordsByTimestampGreaterThanEqual(
                timestamp);
        newerWords.removeAll(olderWords);
        update();
        return newerWords;
    }

    private void update() {
        final LastExport lastExport = new LastExport(Instant.now().toEpochMilli());
        lastExportRepository.save(lastExport);
    }

}
