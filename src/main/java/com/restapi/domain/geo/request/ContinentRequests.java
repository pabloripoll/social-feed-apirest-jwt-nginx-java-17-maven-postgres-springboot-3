package com.restapi.domain.geo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTOs for create/update operations.
 *
 * NOTE: Controller imports these as:
 * com.restapi.domain.geo.request.ContinentRequests.CreateContinentRequest
 * com.restapi.domain.geo.request.ContinentRequests.UpdateContinentRequest
 */
public final class ContinentRequests {
    private ContinentRequests() {}

    public record CreateContinentRequest(
        @NotBlank @Size(max = 64) String name
    ) {}

    public record UpdateContinentRequest(
        @NotBlank @Size(max = 64) String name
    ) {}
}
