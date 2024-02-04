package ovh.miroslaw.kindle2anki.vocabulary.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ovh.miroslaw.kindle2anki.vocabulary.model.Vocabulary;

import java.util.List;

@Repository
public interface VocabularyRepository extends ListCrudRepository<Vocabulary, String> {

    List<Vocabulary> findAllByTimestampGreaterThanEqual(Long timestamp);
}
