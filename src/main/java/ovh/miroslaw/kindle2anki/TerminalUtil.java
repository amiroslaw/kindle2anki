package ovh.miroslaw.kindle2anki;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Utility class for terminal output operations.
 */
public final class TerminalUtil {

    public static final BiFunction<String, AnsiColor, String> ANSI =
            (s, c) -> AnsiOutput.toString(c, s, AnsiColor.DEFAULT);
    public static final BiConsumer<String, AnsiColor> ANSI_PRINT =
            (s, c) -> System.out.println(AnsiOutput.toString(c, s, AnsiColor.DEFAULT));

    private TerminalUtil() {
    }
}
