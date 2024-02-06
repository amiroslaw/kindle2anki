package ovh.miroslaw.kindle2anki.dictionary.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entity class representing update date in dictionary.
 */
@Getter
@NoArgsConstructor
@Entity
public class LastExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long timestamp;

    public LastExport(Long timestamp) {
        this.timestamp = timestamp;
    }
}
