package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ovh.miroslaw.kindle2anki.model.Vocabulary;
import ovh.miroslaw.kindle2anki.model.VocabularyRepository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository wordRepository;

    public Set<String> getVocabulary() {
        return wordRepository.findAll()
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
