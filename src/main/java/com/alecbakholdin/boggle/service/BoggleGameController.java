package com.alecbakholdin.boggle.service;


import com.alecbakholdin.boggle.data.LobbyMap;
import com.alecbakholdin.boggle.model.AddWordRequest;
import com.alecbakholdin.boggle.model.GameStatus;
import com.alecbakholdin.boggle.model.Lobby;
import com.alecbakholdin.boggle.model.Player;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Timer;
import java.util.TimerTask;

@Log4j2
@Controller
@AllArgsConstructor
public class BoggleGameController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LobbyMap lobbyMap;


    @PostMapping("/startGame/{lobbyId}")
    public void startGame(@PathVariable String lobbyId) {
        log.info("New lobby start request: " + lobbyId);
        Lobby lobby = lobbyMap.get(lobbyId);
        if (lobby == null) {
            return;
        }
        lobby.startNewGame();
        int gameDuration = 3 * 60 * 1000;
        lobby.getGame().setGameOverTime(System.currentTimeMillis() + gameDuration);
        scheduleEndGame(lobby, gameDuration);

        simpMessagingTemplate.convertAndSend(lobby.getLobbyTopic("/startGame"), lobby.getGame());
    }


    @MessageMapping("/addWord")
    public void addWord(@Payload AddWordRequest addWordRequest) {
        log.info("New word add request: " + addWordRequest);
        Lobby lobby = lobbyMap.getLobby(addWordRequest.getLobbyId());
        if(lobby == null) return;
        Player player = lobby.getPlayer(addWordRequest.getPlayerId());
        if(player == null) return;
        lobby.addWord(addWordRequest.getPlayerId(), addWordRequest.getWord());
    }

    private void scheduleEndGame(Lobby lobby, int gameDuration) {
        Timer endGameTimer = new Timer();
        endGameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                endGame(lobby);
            }
        }, gameDuration);
    }

    private void endGame(Lobby lobby) {
        lobby.setGameStatus(GameStatus.FINISHED);
        String destination = lobby.getLobbyTopic("/endGame");
        log.info(String.format("Ending game for lobby %s, sending information to %s", lobby.getId(), destination));
        simpMessagingTemplate.convertAndSend(destination, lobby);
    }
}
