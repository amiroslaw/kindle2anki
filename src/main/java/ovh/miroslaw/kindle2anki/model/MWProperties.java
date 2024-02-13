package ovh.miroslaw.kindle2anki.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *  Properties for Merriam-Webster service.
 */
@RequiredArgsConstructor
@Getter
public enum MWProperties {
    EXAMPLE_TEXT("t"), SHORTDEF("shortdef"), ART("artid"), CATEGORY("fl"),
    PRONUNCIATIONS("ipa"), AUDIO("audio"),
    /**
     * The official Merriam-Webster documentation for images provides wrong URL. Alternative links could be helpful:
     * <a href="https://www.merriam-webster.com/assets/mw/static/art/dict/">merriam-webster</a>
     * <a href="http://www.learnersdictionary.com/art/ld/">lerarnersdictionary</a>
     */
    ART_URL("https://merriam-webster.com/assets/ld/images/legacy_print_images/"),
    AUDIO_URL("https://media.merriam-webster.com/audio/prons/en/us/"),
    AUDIO_EXTENSION("ogg");
    private final String value;
}
