package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

public record SearchResponse(
        String query,
        List<SearchResultItem> results
) {
    public record SearchResultItem(
            String type,           // "traffic_sign", "lesson", "question"
            String id,             // Entity ID
            String title,          // Main title in requested language
            String description,    // Brief description
            String href            // Frontend route
    ) {}
}
