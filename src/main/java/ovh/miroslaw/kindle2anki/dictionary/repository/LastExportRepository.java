package ovh.miroslaw.kindle2anki.dictionary.repository;

import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ovh.miroslaw.kindle2anki.dictionary.model.LastExport;

import java.util.Optional;

@Repository
public interface LastExportRepository extends ListCrudRepository<LastExport, Integer> {

    @NativeQuery("SELECT timestamp FROM LastExport ORDER BY id DESC LIMIT 1")
    Optional<Long> findLastTimestamp();

}
