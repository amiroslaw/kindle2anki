package ovh.miroslaw.kindle2anki.vocabulary.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ovh.miroslaw.kindle2anki.vocabulary.model.Vocabulary;

@Repository
public interface VocabularyRepository extends ListCrudRepository<Vocabulary, String> {
    // read lastes
//    SELECT w.stem,l.usage, w.timestamp
//    FROM `WORDS` as w
//    LEFT JOIN `LOOKUPS` as l
//    ON w.id=l.word_key where w.timestamp>""" + str(timestamp) + """;
}