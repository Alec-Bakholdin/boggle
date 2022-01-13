package com.alecbakholdin.boggle.model;

import com.alecbakholdin.boggle.data.WordValidation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class Game implements Serializable {
    private final int size = 5;
    private long gameOverTime;
    private final Board board = new Board(size);

    @JsonIgnore
    @JsonManagedReference
    private final Map<String, Word> wordMap = new HashMap<>();

    public Word createAndAddWord(String wordStr) {
        Word wordObj;
        if(wordMap.containsKey(wordStr)) {
            wordObj = wordMap.get(wordStr);
            wordObj.setDuplicate(true);
            return wordObj;
        }
        wordObj = new Word(wordStr);
        wordObj.setCanBeMade(determineIfWordCanBeMade(wordStr));
        wordObj.setValidWord(WordValidation.isValidWord(wordStr));
        wordMap.put(wordStr, wordObj);
        return wordObj;
    }

    private boolean determineIfWordCanBeMade(String wordStr) {
        if(wordStr.length() <= 0) {
            return false;
        }
        wordStr = wordStr.replace("QU", "Q");
        Set<Point> startLocations = board.getCharLocations(wordStr.charAt(0));
        String nextSubstring = wordStr.substring(1);
        for(Point point : startLocations) {
            if(determineIfWordCanBeMade(nextSubstring, point, Collections.singleton(point))) {
                return true;
            }
        }
        return false;
    }

    private boolean determineIfWordCanBeMade(String wordStr, Point previousLoc, Set<Point> visitedPoints) {
        if(wordStr.length() == 0)
            return true;

        char targetChar = wordStr.charAt(0);
        String nextSubstring = wordStr.substring(1);
        Set<Point> validPoints = getUnvisitedAdjacentLocations(previousLoc, visitedPoints);

        for(Point point : validPoints) {
            if(board.getCharAt(point) != targetChar) continue;
            Set<Point> setIncludingCurrentPoint = new HashSet<>(visitedPoints);
            setIncludingCurrentPoint.add(point);
            if(determineIfWordCanBeMade(nextSubstring, point, setIncludingCurrentPoint)) {
                return true;
            }
        }

        return false;
    }

    private Set<Point> getUnvisitedAdjacentLocations(Point previousLoc, Set<Point> visitedPoints) {
        Set<Point> validPoints = board.getSurroundingPoints(previousLoc);
        validPoints.remove(previousLoc);
        validPoints.removeAll(visitedPoints);
        return validPoints;
    }
}
