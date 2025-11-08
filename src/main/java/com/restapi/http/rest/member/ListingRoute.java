package com.restapi.http.rest.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ListingRoute {

    @GetMapping("/api/v1/members")
    public ResponseEntity<Map<String, String>> register() {
        return ResponseEntity
                .status(200)
                .body(Map.of("message", "Development in progress."));
    }
}
