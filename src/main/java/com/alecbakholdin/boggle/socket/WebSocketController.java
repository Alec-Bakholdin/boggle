package com.alecbakholdin.boggle.socket;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Set;

@Log4j2
@Controller
@AllArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Set<String> connectedUsers = new HashSet<>();

    @MessageMapping("/register")
    @SendToUser("/queue/newMember")
    public String registerNewUser(@Payload String username) {
        log.info("Registering user " + username);
        if(!connectedUsers.contains(username)) {
            connectedUsers.add(username);
            return String.format("Welcome, %s!", username);
        }
        return String.format("Welcome back, %s!", username);
    }

    @MessageMapping("/message")
    public void sendMessage(@Payload Message message) {
        log.info(message);
        simpMessagingTemplate.convertAndSend("/topic/message", message);
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/message", message);
    }
}
