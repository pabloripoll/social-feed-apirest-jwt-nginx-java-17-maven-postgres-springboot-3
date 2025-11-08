package com.restapi.domain.geo.exception;

import com.restapi.http.rest.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Example domain exception for not-found resources.
 */
public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, "resource.not_found");
    }
}