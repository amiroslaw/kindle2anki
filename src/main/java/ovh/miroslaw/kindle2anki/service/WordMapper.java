package ovh.miroslaw.kindle2anki.service;

import ovh.miroslaw.kindle2anki.model.Word;

import java.util.Optional;

public interface WordMapper {

    Optional<Word> map(String json);
}
