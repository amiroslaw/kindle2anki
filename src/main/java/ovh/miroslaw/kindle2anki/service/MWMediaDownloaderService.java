package ovh.miroslaw.kindle2anki.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ovh.miroslaw.kindle2anki.model.MWProperties;
import ovh.miroslaw.kindle2anki.model.Word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;

/**
 * The type Mw media downloader service.
 */
@Service
public class MWMediaDownloaderService implements MediaDownloaderService {

    @Value("${config.path}")
    private String configPath;
    @Value("${anki.collection.path}")
    private String ankiCollectionPath;
    /**
     * Extension according to the doc https://dictionaryapi.com/products/json#sec-4.artl
     */
    public static final String MW_PICTURE_EXTENSION = ".gif";
    public static final String MW_AUDIO_EXTENSION = ".mp3";
    private final RestClient restClient;

    public MWMediaDownloaderService() {
        this.restClient = RestClient.create();
    }

    @Override
    public void downloadMedia(Word word) {
        try {
            Files.createDirectories(Paths.get(ankiCollectionPath));
        } catch (IOException e) {
            ANSI_PRINT.accept("Unable to create directory " + ankiCollectionPath, AnsiColor.RED);
        }
        downloadAudio(word.audio().getFirst());
        if (!word.illustration().isBlank()) {
//            downloadIllustration(word.illustration());
        }
    }

    private void downloadAudio(String fileName) {
        final String pathVariable = getPathVariable(fileName);
        final String url = String.format("%s%s/", MWProperties.AUDIO_URL.getValue(), pathVariable);
        download(url, fileName + MW_AUDIO_EXTENSION);
    }

    private String getPathVariable(String fileName) {
        return switch (fileName) {
            case String name when name.startsWith("bix") ->"bix";
            case String name when name.startsWith("gg") ->"gg";
            case String name when Character.isDigit(name.charAt(0)) ->"number";
            default -> fileName.substring(0, 1);
                // TODO if audio begins with a number or punctuation (eg, "_"), the subdirectory should be "number",
        };
    }

    private void downloadIllustration(String illustration) {
        final String fileName = illustration.substring(0, illustration.lastIndexOf('.')) + MW_PICTURE_EXTENSION;
        download(MWProperties.ART_URL.getValue(), fileName);
    }

    private void download(String baseUrl, String fileName) {
        System.out.println(baseUrl + fileName);
        byte[] imageBytes = restClient.get()
                .uri(baseUrl + fileName)
                .retrieve()
                .body(byte[].class);

        final File file = Paths.get(ankiCollectionPath, fileName).toFile();

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(imageBytes);
        } catch (IOException e) {
            ANSI_PRINT.accept("Unable to create: " + fileName, AnsiColor.RED);
        }
    }
}
