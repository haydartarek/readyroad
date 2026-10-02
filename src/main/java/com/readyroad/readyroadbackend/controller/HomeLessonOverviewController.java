package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.HomeLessonOverviewResponse;
import com.readyroad.readyroadbackend.service.HomeLessonOverviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lessons/home-overview")
public class HomeLessonOverviewController {

    private final HomeLessonOverviewService service;

    public HomeLessonOverviewController(
            HomeLessonOverviewService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<HomeLessonOverviewResponse>> getOverview() {
        return ResponseEntity.ok(service.getOverview());
    }
}
