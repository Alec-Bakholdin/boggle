package com.alecbakholdin.boggle.socket;

import lombok.Data;

@Data
public class Message {
    private String from;
    private String text;
}
