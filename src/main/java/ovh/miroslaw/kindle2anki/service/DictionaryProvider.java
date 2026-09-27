package ovh.miroslaw.kindle2anki.service;

import java.util.Optional;

public interface DictionaryProvider {

    Optional<String> getDefinition(String test);
}
