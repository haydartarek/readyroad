package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.LessonDetailResponse;
import com.readyroad.readyroadbackend.dto.response.LessonResponse;
import com.readyroad.readyroadbackend.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessons")
@Tag(name = "Lessons", description = "Driving theory lessons (public, read-only)")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping
    @Operation(summary = "List all active lessons (summary, no pages)")
    public ResponseEntity<List<LessonResponse>> listLessons() {
        return ResponseEntity.ok(lessonService.getAllActiveLessons());
    }

    @GetMapping("/{idOrCode}")
    @Operation(summary = "Get a single lesson with all pages")
    public ResponseEntity<LessonDetailResponse> getLesson(@PathVariable String idOrCode) {
        return ResponseEntity.ok(lessonService.getLessonByIdOrCode(idOrCode));
    }

    @GetMapping("/search")
    @Operation(summary = "Search lessons by keyword")
    public ResponseEntity<List<LessonResponse>> searchLessons(@RequestParam("q") String query) {
        return ResponseEntity.ok(lessonService.searchLessons(query));
    }

    @GetMapping("/count")
    @Operation(summary = "Total number of active lessons")
    public ResponseEntity<Map<String, Long>> countLessons() {
        return ResponseEntity.ok(Map.of("count", lessonService.countActiveLessons()));
    }
}
