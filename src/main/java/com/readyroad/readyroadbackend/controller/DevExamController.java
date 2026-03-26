package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.DevExamCategoryDto;
import com.readyroad.readyroadbackend.dto.DevExamAnswerCheckDto;
import com.readyroad.readyroadbackend.dto.DevExamAnswerCheckRequest;
import com.readyroad.readyroadbackend.dto.DevExamQuestionDto;
import com.readyroad.readyroadbackend.service.DevExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Developer Skills Assessment", description = "Software engineering knowledge assessment API")
public class DevExamController {

    private final DevExamService devExamService;

    @GetMapping("/categories")
    @Operation(summary = "List all assessment categories", description = "Returns all active developer skill categories with their settings.")
    public ResponseEntity<List<DevExamCategoryDto>> getCategories(
            @RequestParam(defaultValue = "en") String lang) {
        log.info("GET /api/assessment/categories lang={}", lang);
        return ResponseEntity.ok(devExamService.getCategories(lang));
    }

    @GetMapping("/categories/{slug}")
    @Operation(summary = "Get a single assessment category")
    public ResponseEntity<DevExamCategoryDto> getCategory(
            @PathVariable String slug,
            @RequestParam(defaultValue = "en") String lang) {
        return ResponseEntity.ok(devExamService.getCategoryBySlug(slug, lang));
    }

    @GetMapping("/categories/{slug}/questions")
    @Operation(summary = "Get questions for a category and difficulty level", description = "Returns up to `limit` questions (default 3) for the given category slug and level.")
    public ResponseEntity<List<DevExamQuestionDto>> getQuestions(
            @PathVariable String slug,
            @RequestParam String level,
            @RequestParam(defaultValue = "en") String lang,
            @RequestParam(defaultValue = "3") int limit) {
        log.info("GET /api/assessment/categories/{}/questions level={} lang={} limit={}", slug, level, lang, limit);
        return ResponseEntity.ok(devExamService.getQuestions(slug, level, lang, limit));
    }

    @PostMapping("/questions/{questionId}/check")
    @Operation(summary = "Check an assessment answer", description = "Returns whether the submitted choice is correct and which choice is correct for review rendering.")
    public ResponseEntity<DevExamAnswerCheckDto> checkAnswer(
            @PathVariable Long questionId,
            @RequestBody DevExamAnswerCheckRequest request) {
        return ResponseEntity.ok(devExamService.checkAnswer(questionId, request.getChoiceId()));
    }
}
