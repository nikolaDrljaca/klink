package com.drbrosdev.klinkrest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class IndexController {

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ok("Service healthy.");
    }
}
