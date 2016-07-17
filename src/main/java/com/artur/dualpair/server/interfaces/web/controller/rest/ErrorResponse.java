package com.artur.dualpair.server.interfaces.web.controller.rest;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private int statusCode;
    private String message;

    public ErrorResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorResponse from(Exception e, HttpStatus httpStatus) {
        return new ErrorResponse(e.getMessage(), httpStatus.value());
    }
}
