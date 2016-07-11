package com.artur.dualpair.server.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorDTO {

    private String error;

    @JsonProperty("error_description")
    private String errorDescription;

    public ErrorDTO(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

}
