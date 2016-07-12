package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

    @JsonProperty("error_id")
    private String errorId;

    @JsonProperty("error_description")
    private String errorDescription;

    public ErrorResponse(String errorDescription, String errorId) {
        this.errorDescription = errorDescription;
        this.errorId = errorId;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getErrorId() {
        return errorId;
    }

    public static ErrorResponse from(Exception e) {
        return new ErrorResponse(e.getMessage(), e.getMessage());
    }

}
