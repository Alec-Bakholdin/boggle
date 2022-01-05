package com.alecbakholdin.boggle.model;

import lombok.Data;

@Data
public class AddWordRequest {
    private String lobbyId;
    private String playerId;
    private String word;
}
