package ovh.miroslaw.kindle2anki.dictionary.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

/**
 * Entity class representing personal dictionary.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Dictionary {
    @Id
    private String word;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "definitions", joinColumns = @JoinColumn(name = "dictionary_id"))
    @Column(name = "definitions", nullable = false)
    private List<String> definitions;
    private String category;
    private String translation;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "pronunciations", joinColumns = @JoinColumn(name = "dictionary_id"))
    @Column(name = "pronunciations", nullable = false)
    private List<String> pronunciations;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "audios", joinColumns = @JoinColumn(name = "dictionary_id"))
    @Column(name = "audios", nullable = false)
    private List<String> audios;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "examples", joinColumns = @JoinColumn(name = "dictionary_id"))
    @Column(name = "examples", nullable = false)
    private List<String> examples;

    private String illustration;

    public String getDefinition() {
        return getFirstOrEmpty(definitions);
    }

    public String getExample() {
        return getFirstOrEmpty(examples);
    }
    public String getAudio() {
        return getFirstOrEmpty(audios);
    }

    public String getPronunciation() {
        return getFirstOrEmpty(pronunciations);
    }
    private String getFirstOrEmpty(List<String> list) {
        if (list.isEmpty()) {
            return Strings.EMPTY;
        }
        return list.getFirst();
    }
}
