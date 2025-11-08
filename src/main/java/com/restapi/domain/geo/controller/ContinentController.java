package com.restapi.domain.geo.controller;

import com.restapi.domain.geo.dto.ContinentDto;
import com.restapi.domain.geo.dto.ContinentSummaryDto;
import com.restapi.domain.geo.entity.Continent;
import com.restapi.domain.geo.exception.NameConflictException;
import com.restapi.domain.geo.exception.ResourceNotFoundException;
import com.restapi.domain.geo.repository.ContinentRepository;
import com.restapi.domain.geo.request.ContinentRequests.CreateContinentRequest;
import com.restapi.domain.geo.request.ContinentRequests.UpdateContinentRequest;
import com.restapi.domain.geo.service.ContinentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain-level controller that contains the business logic previously as in it would be ContinentServiceImpl
 *
 * NOTE:
 * - This class implements ContinentService so it can be injected anywhere the service interface is expected.
 * - It lives under com.restapi.domain.geo.controller by your request (domain controller, not HTTP controller).
 */
@Service
@Transactional
public class ContinentController implements ContinentService {

    private final ContinentRepository repository;

    public ContinentController(ContinentRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContinentSummaryDto> listAll() {
        return repository.findAll().stream()
            .map(this::toSummaryDto)      // maps to ContinentSummaryDto
            .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Override
    @Transactional(readOnly = true)
    public ContinentSummaryDto getById(Long id) {
        Continent c = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Continent not found: " + id));
        return toSummaryDto(c);
    }

    @Override
    public ContinentDto create(CreateContinentRequest req) {
        if (repository.existsByName(req.name())) {
            throw new NameConflictException("Continent name already exists: " + req.name());
        }
        Continent c = new Continent();
        c.setName(req.name());
        Continent saved = repository.save(c);
        return toDto(saved);
    }

    @SuppressWarnings("null")
    @Override
    public ContinentDto update(Long id, UpdateContinentRequest req) {
        Continent existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Continent not found: " + id));

        // If another entity has the same name, conflict
        repository.findByName(req.name())
            .filter(other -> !other.getId().equals(id))
            .ifPresent(other -> { throw new NameConflictException("Continent name already in use: " + req.name()); });

        existing.setName(req.name());
        Continent saved = repository.save(existing);
        return toDto(saved);
    }

    @SuppressWarnings("null")
    @Override
    public void delete(Long id) {
        Continent existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Continent not found: " + id));
        repository.delete(existing);
    }

    /**
     * --- Mapping helpers --- *
     */

    private ContinentDto toDto(Continent c) {
        return new ContinentDto(c.getId(), c.getName(), c.getCreatedAt(), c.getUpdatedAt());
    }

    private ContinentSummaryDto toSummaryDto(Continent c) {
        return new ContinentSummaryDto(c.getId(), c.getName());
    }
}
