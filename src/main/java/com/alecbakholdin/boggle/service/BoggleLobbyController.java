package com.alecbakholdin.boggle.service;

import com.alecbakholdin.boggle.data.LobbyMap;
import com.alecbakholdin.boggle.data.PlayerLifespanTracker;
import com.alecbakholdin.boggle.model.ApiError;
import com.alecbakholdin.boggle.model.Lobby;
import com.alecbakholdin.boggle.model.Player;
import com.alecbakholdin.boggle.service.errors.InvalidLobbyIdException;
import com.alecbakholdin.boggle.service.errors.InvalidUsernameException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Controller
@RestController
@CrossOrigin
@AllArgsConstructor
public class BoggleLobbyController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LobbyMap lobbyMap;
    private final PlayerLifespanTracker playerLifespanTracker;

    @GetMapping("/getLobby/{lobbyId}")
    public Lobby getLobby(@PathVariable String lobbyId) throws InvalidLobbyIdException {
        log.info(String.format("Fetching lobby %s", lobbyId));
        if(lobbyMap.containsKey(lobbyId)) {
            return lobbyMap.getLobby(lobbyId);
        }
        throw new InvalidLobbyIdException("Lobby doesn't exist");
    }

    @GetMapping("/lobbyExists/{lobbyId}")
    public boolean lobbyExists(@PathVariable String lobbyId) {
        boolean lobbyExists = lobbyMap.containsKey(lobbyId);
        log.info(String.format("Checking if lobby %s exists: %s", lobbyId, lobbyExists));
        return lobbyExists;
    }

    @PostMapping("/createLobby")
    public Lobby createLobby() {
        Lobby lobby = lobbyMap.createBoggleLobby();
        log.info(String.format("Created new lobby with id %s", lobby.getId()));
        return lobby;
    }

    @PostMapping("/joinLobby")
    public Player joinLobby(@RequestBody Player player) throws InvalidLobbyIdException, InvalidUsernameException {
        log.info(String.format("New player joined: %s", player));
        if(player.getUsername() == null || player.getUsername().length() == 0) {
            throw new InvalidUsernameException("Username must be at least 1 character long");
        }
        Lobby lobby = lobbyMap.getLobby(player.getLobbyId());
        if(lobby == null) {
            throw new InvalidLobbyIdException("No lobby found with that id");
        }
        lobby.addPlayer(player);
        playerLifespanTracker.addPlayer(player);
        simpMessagingTemplate.convertAndSend(lobby.getLobbyTopic("/playerJoin"), player);
        return player;
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

    @ExceptionHandler({InvalidLobbyIdException.class, InvalidUsernameException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(Exception ex) {
        return new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), ex.getMessage());
    }

    @ExceptionHandler({UnsupportedOperationException.class})
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiError handleNotImplemented(Exception ex){
        return new ApiError(HttpStatus.NOT_IMPLEMENTED.value(), ex.getClass().getSimpleName(), ex.getMessage());
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnhandledErrors(Exception ex) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getClass().getSimpleName(), ex.getMessage());
    }
}
