package com.alecbakholdin.boggle.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoggleDice {
    private static String[] dieList = {
            "QBZJXK", "TOUOTO", "OVWRGR", "AAAFSR", "AUMEEG",
            "HHLRDO", "NHDTHO", "LHNROD", "AFAISR", "YIFASR",
            "TELPCI", "SSNSEU", "RIYPRH", "DORDLN", "CCWNST",
            "TTOTEM", "SCTIEP", "EANDNN", "MNNEAG", "UOTOWN",
            "AEAEEE", "YIFPSR", "EEEEMA", "ITITIE", "ETILIC"
    };
    private final List<Integer> dieOrder;
    private int index;

    public BoggleDice(int size) {
        this.dieOrder = range(0, dieList.length);
        Collections.shuffle(this.dieOrder);
        this.index = 0;
    }

    public char getChar() {
        int dieIndex = dieOrder.get(index % dieList.length);
        String die = dieList[dieIndex];
        index += 1;
        int charIndex = (int)(Math.random() * 6);
        return die.charAt(charIndex);
    }

    private List<Integer> range(int min, int max) {
        List<Integer> rangeList = new ArrayList<>();
        for(int i = min; i < max; i++) {
            rangeList.add(i);
        }
        return rangeList;
    }
}
