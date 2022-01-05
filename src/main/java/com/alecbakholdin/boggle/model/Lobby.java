package com.alecbakholdin.boggle.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Lobby {
    private String id;
    private Game game;
    private GameStatus gameStatus = GameStatus.INACTIVE;
    @JsonManagedReference
    private Set<Player> players = new HashSet<>();
    @JsonManagedReference
    private Set<Player> removedPlayers = new HashSet<>();

    public void startNewGame() {
        gameStatus = GameStatus.ACTIVE;
        game = new Game();
        players.forEach(player -> player.setWords(new ArrayList<>()));
        removedPlayers.forEach(player -> player.setWords(new ArrayList<>()));
    }

    public String getLobbyTopic(String topic) {
        if(topic.startsWith("/")) {
            topic = topic.substring(1);
        }
        return String.format("/topic/game/%s/%s", id, topic);
    }

    public Player getPlayer(String playerId) {
        List<Player> matchingPlayers = players.stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .collect(Collectors.toList());
        if(matchingPlayers.size() == 1) {
            return matchingPlayers.get(0);
        }
        return null;
    }

    public void addWord(String playerId, String word) {
        Player player = getPlayer(playerId);
        Word wordObj = game.createAndAddWord(word);
        player.addWord(wordObj);
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.setLobby(this);
    }

    public void removePlayer(Player player) {
        removedPlayers.add(player);
        players.remove(player);
    }

    public void deletePlayer(Player player) {
        removedPlayers.remove(player);
        players.remove(player);
    }
}
