package ovh.miroslaw.kindle2anki.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ovh.miroslaw.kindle2anki.dictionary.model.Dictionary;
import ovh.miroslaw.kindle2anki.model.MWProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static ovh.miroslaw.kindle2anki.TerminalUtil.ANSI_PRINT;
import static ovh.miroslaw.kindle2anki.model.MWProperties.AUDIO_EXTENSION;
import static ovh.miroslaw.kindle2anki.model.MWProperties.AUDIO_URL;

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
    private final RestClient restClient;

    public MWMediaDownloaderService() {
        this.restClient = RestClient.create();
    }

    @Override
    public void downloadMedia(Dictionary dictionary) {
        try {
            Files.createDirectories(Paths.get(ankiCollectionPath));
        } catch (IOException e) {
            ANSI_PRINT.accept("Unable to create directory " + ankiCollectionPath, AnsiColor.RED);
        }
        if (!dictionary.getAudios().isEmpty()) {
            downloadAudio(dictionary.getAudios().getFirst());
        }
        if (!dictionary.getIllustration().isBlank()) {
//            downloadIllustration(dictionary.illustration());
        }
    }

    private void downloadAudio(String fileName) {
        final String pathVariable = getPathVariable(fileName);
        final String url = String.format("%s%s/%s/", AUDIO_URL.getValue(), AUDIO_EXTENSION.getValue(), pathVariable);
        download(url, fileName + "." + AUDIO_EXTENSION.getValue());
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
            ANSI_PRINT.accept("Unable to create: " + baseUrl + fileName, AnsiColor.RED);
        }
    }
}
