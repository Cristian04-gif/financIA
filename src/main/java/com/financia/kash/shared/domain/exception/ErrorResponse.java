package com.financia.kash.shared.domain.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private String exception;
    private Map<String, String> errors;

    public ErrorResponse(String message, String exception) {
        this.message = message;
        this.exception = exception;
        this.errors = new HashMap<>();
    }

    public ErrorResponse(String message, String exception, Map<String, String> errors) {
        this.message = message;
        this.exception = exception;
        this.errors = errors;
    }
}
