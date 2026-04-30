package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.LessonDetailResponse;
import com.readyroad.readyroadbackend.dto.response.LessonResponse;
import com.readyroad.readyroadbackend.service.LessonProgressService;
import com.readyroad.readyroadbackend.service.LessonService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessons")
@Tag(name = "Lessons", description = "Driving theory lessons (public, read-only)")
public class LessonController {

    private final LessonService lessonService;
    private final LessonProgressService lessonProgressService;
    private final AuthenticationUtil authenticationUtil;

    public LessonController(LessonService lessonService,
            LessonProgressService lessonProgressService,
            AuthenticationUtil authenticationUtil) {
        this.lessonService = lessonService;
        this.lessonProgressService = lessonProgressService;
        this.authenticationUtil = authenticationUtil;
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

    /**
     * Mark a page as read and track progress.
     * Automatically fires LESSON_PROGRESS + NEXT_STEP notifications when
     * all pages have been read.
     *
     * POST /api/lessons/{idOrCode}/progress
     * Body: { "totalPages": N, "pageNumber": P }
     */
    @PostMapping("/{idOrCode}/progress")
    @Operation(summary = "Mark a page as read (requires authentication)")
    public ResponseEntity<Map<String, Object>> markPageRead(
            @PathVariable String idOrCode,
            @RequestBody Map<String, Integer> body,
            Authentication authentication) {

        Long userId = authenticationUtil.extractUserId(authentication);
        LessonDetailResponse lesson = lessonService.getLessonByIdOrCode(idOrCode);

        int totalPages = body.getOrDefault("totalPages",
                lesson.pages() != null ? lesson.pages().size() : 1);
        Integer pageNumber = body.get("pageNumber");

        Map<String, Object> result = lessonProgressService.markPageRead(
                userId, lesson.id(), totalPages, pageNumber);

        return ResponseEntity.ok(result);
    }

    /**
     * Get the current user's progress for a lesson.
     *
     * GET /api/lessons/{idOrCode}/progress
     */
    @GetMapping("/{idOrCode}/progress")
    @Operation(summary = "Get current user's progress for a lesson (requires authentication)")
    public ResponseEntity<Map<String, Object>> getProgress(
            @PathVariable String idOrCode,
            Authentication authentication) {

        Long userId = authenticationUtil.extractUserId(authentication);
        LessonDetailResponse lesson = lessonService.getLessonByIdOrCode(idOrCode);

        return ResponseEntity.ok(lessonProgressService.getProgress(userId, lesson.id()));
    }
}
