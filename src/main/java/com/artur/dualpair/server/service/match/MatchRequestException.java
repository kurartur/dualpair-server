package com.artur.dualpair.server.service.match;

public class MatchRequestException extends Exception {

    public MatchRequestException(String message) {
        super(message);
    }

    public MatchRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
