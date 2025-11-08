package com.restapi.http.rest.feed;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PostsRoute {

    @GetMapping("/api/v1/feed/posts")
    public ResponseEntity<Map<String, String>> register() {
        return ResponseEntity
                .status(200)
                .body(Map.of("message", "Development in progress."));
    }
}
