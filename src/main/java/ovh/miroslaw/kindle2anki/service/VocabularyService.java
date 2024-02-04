package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ovh.miroslaw.kindle2anki.vocabulary.model.Vocabulary;
import ovh.miroslaw.kindle2anki.vocabulary.repository.VocabularyRepository;

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

    public List<String> getVocabulary(String dateFrom) {
        final long timestamp;
        try {
            timestamp = LocalDate.parse(dateFrom).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
           ANSI_PRINT.accept("Wrong date format. Format: yyyy-MM-dd (2022-01-31)", AnsiColor.RED);
           return Collections.emptyList();
        }
        return vocabularyRepository.findAllByTimestampGreaterThanEqual(timestamp)
                .parallelStream()
                .map(Vocabulary::getStem)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .toList();
    }

    public List<String> getVocabulary() {
        return vocabularyRepository.findAll()
                .parallelStream()
                .map(Vocabulary::getStem)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .toList();
    }

}
