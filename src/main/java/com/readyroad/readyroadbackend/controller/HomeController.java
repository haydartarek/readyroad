// src/main/java/com/readyroad/readyroadbackend/controller/HomeController.java
package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.HomeStatsResponse;
import com.readyroad.readyroadbackend.service.LessonService;
import com.readyroad.readyroadbackend.service.TrafficSignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private static final long EXAM_QUESTION_COUNT = 50;
    private static final long SUPPORTED_LANGUAGES_COUNT = 4;

    private final TrafficSignService trafficSignService;
    private final LessonService lessonService;

    @GetMapping("/")
    public String home() {
        return "ReadyRoad API is running! ✅";
    }

    @GetMapping("/api/home/stats")
    public ResponseEntity<HomeStatsResponse> getHomeStats() {
        return ResponseEntity.ok(new HomeStatsResponse(
                EXAM_QUESTION_COUNT,
                trafficSignService.countActiveSigns(),
                lessonService.countActiveLessons(),
                trafficSignService.countActiveDisplayGroups(),
                SUPPORTED_LANGUAGES_COUNT));
    }
}
