package ovh.miroslaw.kindle2anki.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Root(
        Meta meta,
        HWI hwi,
        String fl,
        List<Artl> artl,
        List<String> shortdef
) {}

//@JsonIgnoreProperties(ignoreUnknown = true)
//public record Root(
//        Meta meta,
//        HWI hwi,
//        String gram,
//        List<Map<String, List<List<Map<String, List<String>>>>>> def,
//        List<URO> uros,
//) {}
@JsonIgnoreProperties(ignoreUnknown = true)
record Meta(
        String id,
        String uuid
) {}

// String src,
// String section,
// Map<String, String> target,
// List<String> stems,
// Map<String, Object> appShortdef,
// boolean offensive

@JsonIgnoreProperties(ignoreUnknown = true)
record HWI(
        List<Pronunciation> prs
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Pronunciation(String ipa, Sound sound) {}
record Sound(String audio) {}

 record Artl(
        String artid,
        String dim
) {}


record Sense(String sn, List<Dt> dt) {}

record Dt(List<TextOrVis> content) {}

record TextOrVis(String type, Object data) {
  TextOrVis {
    if (type.equals("text")) {
      this.data = new Text(data);
    } else if (type.equals("vis")) {
      this.data = new Vis((List<Map<String, String>>) data);
    }
  }
}

record Text(String text) {}

record Vis(List<Map<String, String>> examples) {}
