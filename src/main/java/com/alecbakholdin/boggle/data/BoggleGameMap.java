package com.alecbakholdin.boggle.data;

import com.alecbakholdin.boggle.model.BoggleGame;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

@Component
public class BoggleGameMap extends HashMap<String, BoggleGame> {
    private static final int alphanumericMin = 0;
    private static final int alphanumericMax = 26 + 26 + 10; // lowercase, uppercase, digits
    private static final int ID_LENGTH = 10;

    public String getNewId() {
        String id;
        do{
            id = generateAlphanumericString(ID_LENGTH);
        }while(this.containsKey(id));
        return id;
    }

    private String generateAlphanumericString(int length) {
        Random random = new Random();
        return random.ints(length, alphanumericMin, alphanumericMax)
                .map(this::mapAlphaNumericIntToChar)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private char mapAlphaNumericIntToChar(int alphanumericInt) {
        if(alphanumericInt >= alphanumericMax || alphanumericInt < alphanumericMin) {
            throw new UnsupportedOperationException(String.format("alphanumericInt must be between %d and %d, not including %d, but was %d", alphanumericMin, alphanumericMax, alphanumericMax, alphanumericInt));
        }
        if(alphanumericInt < 26) {
            return (char)(alphanumericInt + 'a');
        }
        if(alphanumericInt < 26 + 26) {
            return (char)(alphanumericInt + 'A' - 26);
        }
        return (char)(alphanumericInt + '0' - 26 - 26);
    }
}
