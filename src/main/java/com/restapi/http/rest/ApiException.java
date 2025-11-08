package com.restapi.http.rest;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Base exception for domain errors that maps to an HTTP status.
 * Concrete domain exceptions should extend this.
 */
public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final Map<String, String> details;

    protected ApiException(HttpStatus status, String message) {
        this(status, message, null, null);
    }

    protected ApiException(HttpStatus status, String message, String code) {
        this(status, message, code, null);
    }

    protected ApiException(HttpStatus status, String message, String code, Map<String, String> details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = details;
    }

    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Optional domain error code, e.g. "continent.name.exists"
     */
    public String getCode() {
        return code;
    }

    /**
     * Optional details map (field -> message).
     */
    public Map<String, String> getDetails() {
        return details;
    }
}
