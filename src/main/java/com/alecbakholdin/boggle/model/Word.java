package com.alecbakholdin.boggle.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class Word {
    @NonNull
    private String word;

    @JsonProperty("score")
    public int getScore() {
        if(!isDuplicate && canBeMade && isValidWord) {
            if(word.length() >= 8) {
                return 11;
            }
            return word.length() - 3;
        } else if(!canBeMade || !isValidWord) {
            return -1;
        }
        return 0;
    }

    private boolean isDuplicate = false;
    private boolean canBeMade = false;
    private boolean isValidWord = false;
}
