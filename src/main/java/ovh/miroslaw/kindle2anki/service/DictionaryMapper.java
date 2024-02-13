package ovh.miroslaw.kindle2anki.service;

import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.model.Tsv;

import java.util.Optional;

public interface DictionaryMapper {

    Optional<Dictionary> map(String json, Tsv tsv);
}
