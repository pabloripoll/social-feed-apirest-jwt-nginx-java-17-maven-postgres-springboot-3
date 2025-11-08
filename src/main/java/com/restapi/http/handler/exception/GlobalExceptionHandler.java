package com.restapi.http.handler.exception;

import com.restapi.http.rest.ApiError;
import com.restapi.http.rest.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.validation.FieldError;
//import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
//import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for HTTP layer.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiError> handleApiException(ApiException ex) {
        HttpStatus status = ex.getStatus();
        ApiError body = new ApiError(
            status.value(),
            status.getReasonPhrase(),
            ex.getCode(),
            ex.getMessage(),
            LocalDateTime.now(),
            ex.getDetails()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ApiError> handleNoHandlerFound(NoHandlerFoundException ex) {
        ApiError body = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            "not_found",
            "Resource not found",
            LocalDateTime.now(),
            Map.of("path", ex.getRequestURL())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        ApiError body = new ApiError(
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
            "method.not_allowed",
            ex.getMessage(),
            LocalDateTime.now(),
            Map.of("method", ex.getMethod() == null ? "unknown" : ex.getMethod())
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex) {
        // ResponseStatusException#getStatusCode() returns HttpStatusCode in newer Spring versions
        HttpStatusCode statusCode = ex.getStatusCode();
        int statusValue = statusCode.value();

        // Try to get a human-friendly reason phrase from HttpStatus if possible
        HttpStatus httpStatus = HttpStatus.resolve(statusValue);
        String reasonPhrase = (httpStatus != null) ? httpStatus.getReasonPhrase() : statusCode.toString();

        ApiError body = new ApiError(
            statusValue,
            reasonPhrase,
            "response.status",
            ex.getReason() != null ? ex.getReason() : ex.getMessage(),
            LocalDateTime.now(),
            null
        );

        // ResponseEntity.status(int) works with the int status code
        return ResponseEntity.status(statusValue).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
            .collect(Collectors.toMap(
                err -> (err instanceof FieldError fe) ? fe.getField() : err.getObjectName(),
                err -> err.getDefaultMessage(),
                (a, b) -> a
            ));
        ApiError body = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "validation.failed",
            "Validation failed for request",
            LocalDateTime.now(),
            errors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                v -> extractPathFromConstraint(v),
                ConstraintViolation::getMessage,
                (a, b) -> a
            ));
        ApiError body = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "validation.constraint_violation",
            "Constraint violations",
            LocalDateTime.now(),
            errors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiError> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiError body = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "request.parse_error",
            "Malformed JSON request",
            LocalDateTime.now(),
            null
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        ApiError body = new ApiError(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            "database.integrity_violation",
            "Database integrity violation",
            LocalDateTime.now(),
            null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiError> handleGeneric(Exception ex) {
        // Log with stacktrace to investigate unexpected errors
        log.error("Unhandled exception caught by GlobalExceptionHandler", ex);
        ApiError body = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "internal.error",
            "Internal server error",
            LocalDateTime.now(),
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // Helper to build friendly key from ConstraintViolation path (e.g. "getById.id")
    private static String extractPathFromConstraint(ConstraintViolation<?> v) {
        String path = v.getPropertyPath().toString();
        int lastDot = path.lastIndexOf('.');
        return lastDot >= 0 ? path.substring(lastDot + 1) : path;
    }
}
