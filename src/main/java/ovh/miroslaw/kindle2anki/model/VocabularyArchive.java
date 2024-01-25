package ovh.miroslaw.kindle2anki.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * Entity class representing vocabulary words from kindle database.
 */
@Getter
@Entity
public class VocabularyArchive {
    @Id
    private String word;
    private String stem;
    private Integer timestamp;
}
