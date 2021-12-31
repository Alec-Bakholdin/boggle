package com.alecbakholdin.boggle.service;

import com.alecbakholdin.boggle.model.Player;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.Random;

public class PlayerController {

    @PostMapping("/player/create")
    public Player createPlayer(@RequestBody Player player, HttpSession session) {
        if(player.getUsername() == null) {
            player.setUsername(String.format("guest_%d", new Random().nextInt(10000)));
        }
        session.setAttribute(Player.PLAYER_ATTRIBUTE, player);
        return player;
    }
}
