package com.restapi.http.rest.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthRoute {

    @GetMapping("/api/v1/auth/register")
    public ResponseEntity<Map<String, String>> register() {
        return ResponseEntity
                .status(200)
                .body(Map.of("message", "Development in progress."));
    }

    @GetMapping("/api/v1/auth/login")
    public ResponseEntity<Map<String, String>> login() {
        return ResponseEntity
                .status(200)
                .body(Map.of("message", "Development in progress."));
    }

    @GetMapping("/api/v1/auth/activation")
    public ResponseEntity<Map<String, String>> activation() {
        return ResponseEntity
                .status(200)
                .body(Map.of("message", "Development in progress."));
    }

    @GetMapping("/api/v1/auth/refresh")
    public ResponseEntity<Map<String, String>> refresh() {
        return ResponseEntity
                .status(200)
                .body(Map.of("message", "Development in progress."));
    }

    @GetMapping("/api/v1/auth/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity
                .status(200)
                .body(Map.of("message", "Development in progress."));
    }

    @GetMapping("/api/v1/auth/whoami")
    public ResponseEntity<Map<String, String>> whoami() {
        return ResponseEntity
                .status(200)
                .body(Map.of("message", "Development in progress."));
    }
}
