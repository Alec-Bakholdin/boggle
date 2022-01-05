package com.alecbakholdin.boggle.data;

import com.alecbakholdin.boggle.model.Player;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Log4j2
@Component
public class PlayerLifespanTracker {

    private final Map<String, Player> players = new HashMap<>();
    private final Map<String, Timer> timers = new HashMap<>();

    @Synchronized
    public void addPlayer(Player player) {
        players.put(player.getPlayerId(), player);
        timers.put(player.getPlayerId(), createNewTimer(player));
    }

    @Synchronized
    public void refreshPlayerTimer(String playerId) {
        Timer oldTimer = timers.get(playerId);
        Player player = players.get(playerId);
        if(oldTimer == null || player == null) {
            log.info(String.format("One of these was null, oldTimer: %s, player: %s", oldTimer, player));
            return;
        }
        oldTimer.cancel();
        timers.put(playerId, createNewTimer(player));
    }

    private Timer createNewTimer(Player player) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                log.info(String.format("Removing player: %s", player));
                player.removeFromLobby();
                timers.remove(player.getPlayerId());
                players.remove(player.getPlayerId());
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 3000);
        return timer;
    }
}
