package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

    @JsonProperty("error_id")
    private String errorId;

    @JsonProperty("error_description")
    private String errorDescription;

    public ErrorResponse(String error, String errorDescription) {
        this.errorId = error;
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return errorId;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

}
