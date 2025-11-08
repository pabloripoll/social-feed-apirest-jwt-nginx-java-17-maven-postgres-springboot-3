package com.restapi.http.rest.geo;

import com.restapi.domain.geo.controller.ContinentController;
import com.restapi.domain.geo.dto.ContinentDto;
import com.restapi.domain.geo.dto.ContinentSummaryDto;
import com.restapi.domain.geo.request.ContinentRequests.CreateContinentRequest;
import com.restapi.domain.geo.request.ContinentRequests.UpdateContinentRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * HTTP route/controller that only handles request/response concerns (validation, mapping of path vars/body)
 * and delegates to the domain controller (com.restapi.domain.geo.controller.ContinentController).
 *
 * This class wraps domain return values into proper ResponseEntity objects (201 for create with Location,
 * 200 for reads/updates and 204 for delete). Business logic and validation rules remain in the domain controller.
 */
@RestController
@RequestMapping("/api/v1/geo/continents")
public class ContinentRoute {

    private final ContinentController continentController;

    public ContinentRoute(ContinentController continentController) {
        this.continentController = continentController;
    }

    @GetMapping
    public ResponseEntity<List<ContinentSummaryDto>> listAll() {
        return ResponseEntity.ok(continentController.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContinentSummaryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(continentController.getById(id));
    }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<ContinentDto> create(@Valid @RequestBody CreateContinentRequest req) {
        ContinentDto created = continentController.create(req);
        URI location = URI.create("/api/v1/geo/continents/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContinentDto> update(@PathVariable Long id, @Valid @RequestBody UpdateContinentRequest req) {
        ContinentDto updated = continentController.update(id, req);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        continentController.delete(id);
        return ResponseEntity.noContent().build();
    }
}
