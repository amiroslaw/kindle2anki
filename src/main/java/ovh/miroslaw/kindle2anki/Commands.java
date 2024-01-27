package ovh.miroslaw.kindle2anki;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ovh.miroslaw.kindle2anki.model.Word;
import ovh.miroslaw.kindle2anki.service.DictionaryProvider;
import ovh.miroslaw.kindle2anki.service.DictionaryService;
import ovh.miroslaw.kindle2anki.service.VocabularyService;
import ovh.miroslaw.kindle2anki.service.WordMapper;

import java.util.Optional;
import java.util.Set;

@Command
@RequiredArgsConstructor
public class Commands {

    private final DictionaryProvider mwClient;
    private final WordMapper mapper;
    private final VocabularyService vocabularyService;
    private final DictionaryService dictionaryService;

    @Command(description = "Get word definition", alias = "d")
    public String definition(@Option(shortNames = 's', required = true) String searchWord) {
        String json = mwClient.getDefinition(searchWord);
        Optional<Word> word = mapper.map(json);
        word.ifPresent(w -> dictionaryService.save(w, searchWord));
        return searchWord;
    }

    @Command(description = "Convert words from kindle database", alias = "k")
    public Set<String> database() {

        return vocabularyService.getVocabulary();
    }

}
