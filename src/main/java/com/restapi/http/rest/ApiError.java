package com.restapi.http.rest;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response returned by GlobalExceptionHandler.
 *
 * Fields will be serialized to snake_case due to your global Jackson config.
 */
public class ApiError {
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private final Map<String, String> errors;

    public ApiError(int status, String error, String code, String message, LocalDateTime timestamp, Map<String, String> errors) {
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    // Getters (Jackson will use them)
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, String> getErrors() { return errors; }
}
