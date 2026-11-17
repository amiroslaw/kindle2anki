package ovh.miroslaw.kindle2anki;

import java.util.function.Consumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Kindle2ankiApplication {

    public static void main(String[] args) {
        Consumer<Boolean>  switchInteractive = (enabled) -> System.setProperty("spring.shell.interactive.enabled", enabled.toString());
        if (args != null && args.length > 0) {
            switchInteractive.accept(false);
        } else {
            switchInteractive.accept(true);
        }
        SpringApplication.run(Kindle2ankiApplication.class, args);
    }

}
