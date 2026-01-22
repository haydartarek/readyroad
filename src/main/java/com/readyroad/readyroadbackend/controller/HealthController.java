package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Health check and status endpoints")
public class HealthController {

    @GetMapping("/health")
    @Operation(
            summary = "Check API health status",
            description = "Returns the current health status of the ReadyRoad backend API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API is healthy and running"),
            @ApiResponse(responseCode = "500", description = "API is down or experiencing issues")
    })
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
