package com.alecbakholdin.boggle.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class BoggleGame implements Serializable {
    private final BoggleBoard boggleBoard = new BoggleBoard(5);
    private final List<Player> players = new ArrayList<>();
    private final String id;
}
