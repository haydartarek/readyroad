package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse response = new HealthResponse(
                "UP",
                "Ready Road Backend is running",
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "0.0.1-SNAPSHOT"
        );
        return ResponseEntity.ok(response);
    }
}

