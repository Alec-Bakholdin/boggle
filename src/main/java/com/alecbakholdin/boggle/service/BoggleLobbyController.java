package com.alecbakholdin.boggle.service;

import com.alecbakholdin.boggle.data.LobbyMap;
import com.alecbakholdin.boggle.data.PlayerLifespanTracker;
import com.alecbakholdin.boggle.model.Lobby;
import com.alecbakholdin.boggle.model.Player;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Controller
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://74.102.204.78:3000"})
@AllArgsConstructor
public class BoggleLobbyController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LobbyMap lobbyMap;
    private final PlayerLifespanTracker playerLifespanTracker;

    @PostMapping("/createLobby")
    public Lobby createLobby() {
        Lobby lobby = lobbyMap.createBoggleLobby();
        log.info(String.format("Created new lobby with id %s", lobby.getId()));
        return lobby;
    }

    @MessageMapping("/joinLobby")
    public void joinLobby(@Payload Player player) {
        log.info(String.format("New lobby join request: %s", player.toString()));
        String lobbyId = player.getLobbyId();

        Lobby lobby = lobbyMap.getLobby(lobbyId);
        lobby.addPlayer(player);
        playerLifespanTracker.addPlayer(player);

        simpMessagingTemplate.convertAndSendToUser(player.getUsername(), "/joinLobby", lobby);
        simpMessagingTemplate.convertAndSend(lobby.getLobbyTopic("/playerJoin"), player);
    }

    @MessageMapping("/heartbeat")
    public void maintainConnection(@Payload String playerId) {
        playerLifespanTracker.refreshPlayerTimer(playerId);
    }

    @Scheduled(fixedRate = 1000)
    public void testFixedRateTask() {
        for(Lobby lobby : lobbyMap.values()) {
            for(Player removedPlayer : lobby.getRemovedPlayers()) {
                String topic = String.format("/topic/game/%s/playerLeave", lobby.getId());
                simpMessagingTemplate.convertAndSend(topic, removedPlayer);
                lobby.deletePlayer(removedPlayer);
            }
        }
    }

}
