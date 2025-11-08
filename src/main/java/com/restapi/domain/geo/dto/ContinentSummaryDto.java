package com.restapi.domain.geo.dto;

import com.restapi.domain.geo.entity.Continent;

/**
 * Lightweight response DTO for Continent — no timestamps.
 */
public record ContinentSummaryDto(
    Long id,
    String name
) {
    public static ContinentSummaryDto fromEntity(Continent c) {
        return new ContinentSummaryDto(c.getId(), c.getName());
    }
}
