package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ovh.miroslaw.kindle2anki.vocabulary.model.Vocabulary;
import ovh.miroslaw.kindle2anki.vocabulary.repository.VocabularyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    public List<String> getVocabulary() {
        return vocabularyRepository.findAll()
                .parallelStream()
                .map(Vocabulary::getStem)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .toList();
    }

}
