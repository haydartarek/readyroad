package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.service.AdminLearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/learning")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminLearningController {

    private final AdminLearningService service;

    @GetMapping("/users")
    public ResponseEntity<?> users(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.students(q, page, size));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> user(@PathVariable long userId) {
        return ResponseEntity.ok(service.student(userId));
    }

    @GetMapping("/exams")
    public ResponseEntity<?> recentExams(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.exams(userId, page, size));
    }

    @GetMapping("/users/{userId}/exams")
    public ResponseEntity<?> exams(@PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.exams(userId, page, size));
    }

    @GetMapping("/users/{userId}/exams/{examType}/{examId}")
    public ResponseEntity<?> examDetail(@PathVariable long userId,
            @PathVariable String examType, @PathVariable long examId) {
        return ResponseEntity.ok(service.examDetail(userId, examType, examId));
    }

    @GetMapping("/users/{userId}/practices")
    public ResponseEntity<?> practices(@PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.practices(userId, page, size));
    }

    @GetMapping("/users/{userId}/lessons")
    public ResponseEntity<?> lessons(@PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.lessons(userId, page, size));
    }

    @GetMapping("/users/{userId}/categories")
    public ResponseEntity<?> categories(@PathVariable long userId) {
        return ResponseEntity.ok(service.categories(userId));
    }

    @GetMapping("/users/{userId}/coverage")
    public ResponseEntity<?> coverage(@PathVariable long userId) {
        return ResponseEntity.ok(service.coverage(userId));
    }

    @GetMapping("/users/{userId}/difficulty")
    public ResponseEntity<?> difficulty(@PathVariable long userId) {
        return ResponseEntity.ok(service.difficulty(userId));
    }

    @GetMapping("/users/{userId}/signs")
    public ResponseEntity<?> signs(@PathVariable long userId) {
        return ResponseEntity.ok(service.signs(userId));
    }

    @GetMapping("/users/{userId}/error-patterns")
    public ResponseEntity<?> errorPatterns(@PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.errorPatterns(userId, page, size));
    }

    @GetMapping("/users/{userId}/activity-availability")
    public ResponseEntity<?> activityAvailability(@PathVariable long userId) {
        return ResponseEntity.ok(service.activityAvailability(userId));
    }
}
