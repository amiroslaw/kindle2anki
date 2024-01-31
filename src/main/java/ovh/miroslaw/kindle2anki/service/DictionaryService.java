package ovh.miroslaw.kindle2anki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.dictionary.repository.DictionaryRepository;
import ovh.miroslaw.kindle2anki.model.Word;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    @Transactional
    public void save(Word word, String searchWord) {
        final Dictionary dictionary = new Dictionary(searchWord,
                word.definition(),
                word.category(),
                word.translation(),
                word.pronunciation(),
                word.audio(),
                word.examples(),
                word.illustration());
        System.out.println(dictionary);
        final Dictionary save = dictionaryRepository.save(dictionary);
        System.out.println(save.getCategory());
    }

    @Transactional
    public List<Dictionary> getDictionary() {
        return dictionaryRepository.findAll();
    }

}
