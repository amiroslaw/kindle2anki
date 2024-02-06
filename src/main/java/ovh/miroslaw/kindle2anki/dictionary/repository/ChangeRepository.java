package ovh.miroslaw.kindle2anki.dictionary.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ovh.miroslaw.kindle2anki.dictionary.model.Change;

import java.util.Optional;

@Repository
public interface ChangeRepository extends ListCrudRepository<Change, Integer> {

    @Query(value = "SELECT timestamp FROM Change ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Long> findLastTimestamp();

}
