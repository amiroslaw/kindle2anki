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
@Table(name = "WORDS")
public class Vocabulary {
    @Id
    private String id;
    private String stem;
    private Integer timestamp;
}
