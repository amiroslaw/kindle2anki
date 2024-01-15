package ovh.miroslaw.kindle2anki;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;

@Command
@RequiredArgsConstructor
public class Commands {

    private final DictionaryProvider mwClient;

    @Command(alias = "t")
    public String test() {
        return mwClient.getDefinition("advance");
    }
}
