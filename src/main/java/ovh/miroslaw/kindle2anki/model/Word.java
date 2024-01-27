package ovh.miroslaw.kindle2anki.model;

import java.util.List;

// maybe delete
public record Word(List<String> definition, String category, List<String> audio, List<String> pronunciation,
                   List<String> examples, String illustration) {
}
