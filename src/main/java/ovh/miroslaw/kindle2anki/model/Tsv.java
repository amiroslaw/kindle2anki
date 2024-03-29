package ovh.miroslaw.kindle2anki.model;

import org.apache.logging.log4j.util.Strings;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;

import java.util.Optional;

public record Tsv(String word, String translation) {

    public Tsv(String word) {
        this(word, Strings.EMPTY);
    }

    public static Tsv fromDictionary(Dictionary dictionary) {
        return new Tsv(dictionary.getWord(), dictionary.getTranslation());
    }

    /**
     * Converts a line of text into a Tsv object.
     *
     * @param line the input line to be converted
     * @return an Optional containing the Tsv object, if string not empty
     */
    public static Optional<Tsv> lineToObject(String line) {
        String[] cols = line.split("\t");
        if (cols.length == 0) {
            return Optional.empty();
        }
        String translation = Strings.EMPTY;
        if (cols.length > 1) {
            translation = cols[1];
        }
        return Optional.of(new Tsv(cols[0], translation));
    }
}
