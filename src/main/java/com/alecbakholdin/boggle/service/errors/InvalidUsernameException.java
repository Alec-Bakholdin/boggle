package com.alecbakholdin.boggle.service.errors;

public class InvalidUsernameException extends Throwable {
    public InvalidUsernameException(String s) {
        super(s);
    }
}
