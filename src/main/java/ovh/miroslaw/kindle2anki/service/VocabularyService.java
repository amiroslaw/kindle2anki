package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ovh.miroslaw.kindle2anki.vocabulary.model.Vocabulary;
import ovh.miroslaw.kindle2anki.vocabulary.repository.VocabularyRepository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    public Set<String> getVocabulary() {
        return vocabularyRepository.findAll()
                .stream()
                .map(Vocabulary::getStem)
                .collect(Collectors.toSet());
    }

    public void get() {
        final Map<String, Long> collect = this.getVocabulary()
                .stream()
                .collect(Collectors.groupingBy(
                                s -> s,
                                Collectors.counting()
                        )
                );
        System.out.println(collect);
    }
}
