package com.artur.dualpair.server.interfaces.web.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class InsufficientPrivilegesException extends RuntimeException {

    public static final String illegalAccess = "Illegal access";

    public InsufficientPrivilegesException(String message) {
        super(message);
    }

    public InsufficientPrivilegesException(String message, Throwable cause) {
        super(message, cause);
    }
}
