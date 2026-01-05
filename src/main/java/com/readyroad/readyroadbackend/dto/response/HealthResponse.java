package com.readyroad.readyroadbackend.dto.response;

public record HealthResponse(
        String status,
        String message,
        String timestamp,
        String version
) {
}

