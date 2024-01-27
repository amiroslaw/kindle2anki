package ovh.miroslaw.kindle2anki.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MWProperties {
    EXAMPLE_TEXT("t"), SHORTDEF("shortdef"), ART("artid"), CATEGORY("fl"),
    PRONUNCIATIONS("ipa"), AUDIO("audio"),
    ART_URL("https://www.merriam-webster.com/assets/mw/static/art/dict/"),
    AUDIO_URL("https://media.merriam-webster.com/audio/prons/en/us/mp3/");
    //    DEFINITION("def"), SEQUENCE("sseq"), SENSE("sense"), DEFINITION_TEXT("dt"), EXAMPLE("vis"), PRS("prs"),
    private final String value;
}
