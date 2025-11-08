package com.restapi.domain.geo.service;

import com.restapi.domain.geo.dto.ContinentDto;
import com.restapi.domain.geo.dto.ContinentSummaryDto;
import com.restapi.domain.geo.request.ContinentRequests.CreateContinentRequest;
import com.restapi.domain.geo.request.ContinentRequests.UpdateContinentRequest;

import java.util.List;

public interface ContinentService {
    List<ContinentSummaryDto> listAll();
    ContinentSummaryDto getById(Long id);
    ContinentDto create(CreateContinentRequest req);
    ContinentDto update(Long id, UpdateContinentRequest req);
    void delete(Long id);
}
