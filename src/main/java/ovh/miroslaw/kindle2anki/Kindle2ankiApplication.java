package ovh.miroslaw.kindle2anki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class Kindle2ankiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Kindle2ankiApplication.class, args);
    }

}
