package com.alecbakholdin.boggle.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Board extends ArrayList<String> {
    private final int size;
    private final Map<Character, Set<Point>> charLocations = new HashMap<>();

    public Board(int size){
        this.size = size;
        initializeBoard();
    }

    public Set<Point> getCharLocations(char c) {
        if(charLocations.containsKey(c)) {
            return charLocations.get(c);
        }
        return new HashSet<>();
    }

    public HashSet<Point> getSurroundingPoints(Point origin) {
        int i = origin.getI();
        int j = origin.getJ();
        List<Point> surroundingPointsList = Arrays.asList(
                new Point(i - 1, j - 1),
                new Point(i - 1, j),
                new Point(i - 1, j + 1),
                new Point(i, j - 1),
                new Point(i, j + 1),
                new Point(i + 1, j - 1),
                new Point(i + 1, j),
                new Point(i + 1, j + 1)
        );
        return surroundingPointsList.stream()
                    .filter(p -> p.getI() >= 0 && p.getJ() >= 0 && p.getI() < size && p.getJ() < size)
                    .collect(Collectors.toCollection(HashSet::new));
    }

    public char getCharAt(Point loc) {
        return this.get(loc.getI()).charAt(loc.getJ());
    }

    private void initializeBoard(){
        BoggleDice boggleDice = new BoggleDice(size);
        for(int i = 0; i < size; i++) {
            StringBuilder row = new StringBuilder();
            for(int j = 0; j < size; j++) {
                char boggleChar = boggleDice.getChar();
                row.append(boggleChar);
                addPointToMap(boggleChar, i, j);
            }
            this.add(row.toString());
        }
    }

    private void addPointToMap(char c, int i, int j) {
        if(!charLocations.containsKey(c)) {
            charLocations.put(c, new HashSet<>());
        }
        Point point = new Point(i, j);
        charLocations.get(c).add(point);
    }
}
