package ovh.miroslaw.kindle2anki.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MWProperties {
    EXAMPLE_TEXT("t"), SHORTDEF("shortdef"), ART("artid"), CATEGORY("fl"),
    PRONUNCIATIONS("ipa"), AUDIO("audio");
    //    DEFINITION("def"), SEQUENCE("sseq"), SENSE("sense"), DEFINITION_TEXT("dt"), EXAMPLE("vis"), PRS("prs"),
    private final String value;
}
