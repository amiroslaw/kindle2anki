package ovh.miroslaw.kindle2anki;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ovh.miroslaw.kindle2anki.model.Word;
import ovh.miroslaw.kindle2anki.service.DictionaryProvider;
import ovh.miroslaw.kindle2anki.service.WordMapper;

import java.util.Optional;

@Command
@RequiredArgsConstructor
public class Commands {

    private final DictionaryProvider mwClient;
    private final WordMapper mapper;

    @Command(alias = "t")
    public String test() {
        String json = mwClient.getDefinition("milk");
        Optional<Word> word = mapper.map(json);
        return word.get().toString();
    }
}
