package com.alecbakholdin.boggle.model;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class BoggleBoard extends ArrayList<String> {
    private final int size;

    public BoggleBoard(int size){
        this.size = size;
        initializeBoard();
    }

    private void initializeBoard(){
        BoggleDice boggleDice = new BoggleDice(size);
        for(int i = 0; i < size; i++) {
            StringBuilder row = new StringBuilder();
            for(int j = 0; j < size; j++) {
                row.append(boggleDice.getChar());
            }
            this.add(row.toString());
        }
    }
}
