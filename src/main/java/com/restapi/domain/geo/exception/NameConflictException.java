package com.restapi.domain.geo.exception;

import com.restapi.http.rest.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Example domain exception for unique constraint / business conflict.
 */
public class NameConflictException extends ApiException {
    public NameConflictException(String message) {
        super(HttpStatus.CONFLICT, message, "resource.name_conflict");
    }
}
