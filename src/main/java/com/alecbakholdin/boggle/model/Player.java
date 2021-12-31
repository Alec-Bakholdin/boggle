package com.alecbakholdin.boggle.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    public static String PLAYER_ATTRIBUTE = "PlayerObj";

    public String username;
    public List<String> words = new ArrayList<>();
}
