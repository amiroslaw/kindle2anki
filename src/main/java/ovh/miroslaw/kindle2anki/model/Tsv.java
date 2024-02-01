package ovh.miroslaw.kindle2anki.model;

import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

public record Tsv(String word, String translation) {

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
