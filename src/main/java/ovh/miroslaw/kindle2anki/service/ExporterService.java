package ovh.miroslaw.kindle2anki.service;

import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;

import java.util.List;
import java.util.Set;

public interface ExporterService {

    void exportVocabulary(Set<String> vocab);

    void exportDictionary(List<Dictionary> words);
}
