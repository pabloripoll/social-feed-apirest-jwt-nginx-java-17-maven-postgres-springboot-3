package com.restapi.http.handler.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.restapi.http.rest.ApiException;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralized exception handler that:
 * - Converts ApiException into a structured JSON error for requests under /api/*
 * - Returns plain text / simple responses for non-API requests (HTML/static)
 * - Handles validation and other common error types with appropriate HTTP status codes
 *
 * Place this class in the same package as your controllers (or a package scanned by Spring).
 */
@SuppressWarnings("null")
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiResourceExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiResourceExceptionHandler.class);

    private boolean isApiRequest(HttpServletRequest request) {
        if (request == null) return true;
        String path = request.getRequestURI();
        return path != null && path.startsWith("/api/");
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex, HttpServletRequest request) {
        log.debug("Handling ApiException: status={}, code={}, path={}",
                ex.getStatus(), ex.getCode(), request == null ? null : request.getRequestURI());

        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.BAD_REQUEST;

        if (!isApiRequest(request)) {
            // For non-API requests return plain text so static/html endpoints are unaffected
            return ResponseEntity.status(status)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(ex.getMessage());
        }

        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                ex.getCode(),
                ex.getDetails(),
                request != null ? request.getRequestURI() : null,
                OffsetDateTime.now()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.debug("No handler found for request: {}", request == null ? null : request.getRequestURI());

        HttpStatus status = HttpStatus.NOT_FOUND;

        if (!isApiRequest(request)) {
            // For non-API: let caller get a simple plain-text 404 (so HTML/static fallbacks work)
            return ResponseEntity.status(status)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Not Found");
        }

        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Resource not found",
                null,
                null,
                request != null ? request.getRequestURI() : null,
                OffsetDateTime.now()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.debug("Validation failed: {}", ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Extract field errors
        Map<String, String> details = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fe : fieldErrors) {
            details.put(fe.getField(), fe.getDefaultMessage());
        }

        if (!isApiRequest(request)) {
            // Non-API: return plain text summary
            return ResponseEntity.status(status)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Validation failed: " + details.toString());
        }

        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Validation failed",
                "validation.error",
                details,
                request != null ? request.getRequestURI() : null,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleBadRequestBody(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.debug("Malformed request body: {}", ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (!isApiRequest(request)) {
            return ResponseEntity.status(status)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Bad request");
        }

        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Malformed request body",
                "request.body.malformed",
                null,
                request != null ? request.getRequestURI() : null,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for path {}: {}", request == null ? null : request.getRequestURI(), ex.getMessage(), ex);

        HttpStatus status = HttpStatus.NOT_FOUND;//HttpStatus.INTERNAL_SERVER_ERROR;

        if (!isApiRequest(request)) {
            // Non-API path: prefer plain text / HTML
            return ResponseEntity.status(status)
                    .contentType(MediaType.TEXT_PLAIN)
                    //.body("Internal Server Error");
                    .body("Not Found");
        }

        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "An unexpected error occurred.",
                null,
                null,
                request != null ? request.getRequestURI() : null,
                OffsetDateTime.now()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(body, headers, status);
    }

    /**
     * Simple DTO used as the JSON payload for API errors.
     * It's declared as a static nested class so a single file can be dropped in.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiErrorResponse {
        private final int status;
        private final String error;
        private final String message;
        private final String code;
        private final Map<String, String> details;
        private final String path;
        private final OffsetDateTime timestamp;

        public ApiErrorResponse(int status,
                                String error,
                                String message,
                                String code,
                                Map<String, String> details,
                                String path,
                                OffsetDateTime timestamp) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.code = code;
            this.details = details;
            this.path = path;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }

        public Map<String, String> getDetails() {
            return details;
        }

        public String getPath() {
            return path;
        }

        public OffsetDateTime getTimestamp() {
            return timestamp;
        }
    }
}
