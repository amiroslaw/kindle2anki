package ovh.miroslaw.kindle2anki.dictionary.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;

@Repository
public interface DictionaryRepository extends ListCrudRepository<Dictionary, String> {
}
