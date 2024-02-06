package ovh.miroslaw.kindle2anki.vocabulary.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ovh.miroslaw.kindle2anki.vocabulary.model.Vocabulary;

import java.util.List;

@Repository
public interface VocabularyRepository extends ListCrudRepository<Vocabulary, String> {

    @Query("SELECT DISTINCT v.stem FROM Vocabulary v ORDER BY v.stem")
    List<String> findDistinctOrderedAllWords();

    @Query("SELECT DISTINCT v.stem FROM Vocabulary v WHERE v.timestamp >= :timestamp ORDER BY v.stem")
    List<String> findDistinctOrderedAllWordsByTimestampGreaterThanEqual(@Param("timestamp") long timestamp);

    @Query("SELECT DISTINCT v.stem FROM Vocabulary v WHERE v.timestamp < :timestamp ORDER BY v.stem")
    List<String> findDistinctOrderedAllWordsByTimestampLessThan(@Param("timestamp") long timestamp);

}
