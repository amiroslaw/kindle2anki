package ovh.miroslaw.kindle2anki.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Optional;

@HttpExchange()
public interface MWDictionaryProvider extends DictionaryProvider {

    @GetExchange("{word}")
    @Override
    Optional<String> getDefinition(@PathVariable String word);
}
