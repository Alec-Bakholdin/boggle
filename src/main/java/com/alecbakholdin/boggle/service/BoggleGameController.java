package com.alecbakholdin.boggle.service;

import com.alecbakholdin.boggle.data.BoggleGameMap;
import com.alecbakholdin.boggle.model.BoggleGame;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Log4j2
@RestController
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@AllArgsConstructor
public class BoggleGameController {
    private final BoggleGameMap boggleGameMap;
    private static final String GAME_ID_ATTRIBUTE = "BoggleGameId";

    @GetMapping("/game/create")
    public BoggleGame createGame(HttpSession session) {
        BoggleGame newGame = new BoggleGame(boggleGameMap.getNewId());
        boggleGameMap.put(newGame.getId(), newGame);
        session.setAttribute(GAME_ID_ATTRIBUTE, newGame);
        return newGame;
    }

    @GetMapping("/game/{id}")
    public BoggleGame getGame(@PathVariable String id) {
        if(boggleGameMap.containsKey(id)) {
            return boggleGameMap.get(id);
        }
        throw new UnsupportedOperationException("This id does not exist");
    }
    
    @PostMapping("/game/{id}")
    public BoggleGame joinGame(@PathVariable String id, HttpSession session) {
        if(boggleGameMap.containsKey(id)) {
            session.setAttribute(GAME_ID_ATTRIBUTE, id);
            return boggleGameMap.get(id);
        }
        throw new UnsupportedOperationException("This id does not exist");
    }
}
