package com.artur.dualpair.server.interfaces.web.controller.rest;

public class ForbiddenException extends RuntimeException {

    public static final String illegalAccess = "Illegal access";

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
