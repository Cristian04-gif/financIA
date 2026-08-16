package com.financia.kash.shared.domain.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private String exception;
    private String path;
    private Map<String, String> errors;

    public ErrorResponse(String message, String exception, String path) {
        this.message = message;
        this.exception = exception;
        this.path = path;
        this.errors = new HashMap<>();
    }

    public ErrorResponse(String message, String exception, String path, Map<String, String> errors) {
        this.message = message;
        this.exception = exception;
        this.path = path;
        this.errors = errors;
    }
}
