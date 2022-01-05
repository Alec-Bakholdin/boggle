package com.alecbakholdin.boggle.data;

import com.alecbakholdin.boggle.model.Lobby;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
public class LobbyMap extends HashMap<String, Lobby> {
    public Lobby createBoggleLobby() {
        String uuid = getNewId();
        Lobby lobby = new Lobby();
        lobby.setId(uuid);
        this.put(uuid, lobby);
        return lobby;
    }

    private String getNewId() {
        String uuid;
        do{
            uuid = UUID.randomUUID().toString();
        }while(this.containsKey(uuid));
        return uuid;
    }

    public Lobby getLobby(String lobbyId) {
        if(!this.containsKey(lobbyId)){
            throw new UnsupportedOperationException(String.format("BoggleLobby %s does not exist", lobbyId));
        }
        return this.get(lobbyId);
    }
}
