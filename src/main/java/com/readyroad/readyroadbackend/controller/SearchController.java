package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.SearchResponse;
import com.readyroad.readyroadbackend.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Global search across traffic signs, lessons, and questions")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Global search", description = "Search across traffic signs, lessons, and exam questions")
    public ResponseEntity<SearchResponse> search(
            @RequestParam("q") String query,
            @RequestParam(value = "lang", defaultValue = "en") String language
    ) {
        SearchResponse results = searchService.search(query, language);
        return ResponseEntity.ok(results);
    }
}

