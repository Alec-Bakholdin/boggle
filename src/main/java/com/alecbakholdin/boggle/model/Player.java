package com.alecbakholdin.boggle.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class Player {
    private String playerId = UUID.randomUUID().toString();
    private boolean isHost = false;
    private String username;
    private String lobbyId;

    private List<Word> words = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private Lobby lobby;

    public void removeFromLobby() {
        if(lobby != null){
            lobby.removePlayer(this);
        }
    }

    public void addWord(Word word) {
        words.add(word);
    }

    @JsonProperty("totalScore")
    private int totalScore() {
        return words.stream()
                .map(Word::getScore)
                .reduce(0, Integer::sum);
    }

    @JsonProperty("words")
    private List<Word> getSortedWords() {
        List<Word> duplicateWords = getWordList(true);
        List<Word> nonDuplicateWords = getWordList(false);
        nonDuplicateWords.addAll(duplicateWords);
        return nonDuplicateWords;
    }

    private List<Word> getWordList(boolean getDuplicates) {
        return words.stream()
                .filter(word -> word.isDuplicate() == getDuplicates)
                .sorted((a, b) -> b.getWord().length() - a.getWord().length())
                .collect(Collectors.toList());
    }
}
