package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.ExamQuestion;
import com.readyroad.readyroadbackend.dto.response.ExamQuestionResponse;
import com.readyroad.readyroadbackend.mapper.ExamQuestionMapper;
import com.readyroad.readyroadbackend.service.ExamQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-questions")
@Tag(name = "Exam Questions", description = "Official exam questions for driving test preparation")
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;
    private final ExamQuestionMapper examQuestionMapper;

    public ExamQuestionController(ExamQuestionService examQuestionService,
                                 ExamQuestionMapper examQuestionMapper) {
        this.examQuestionService = examQuestionService;
        this.examQuestionMapper = examQuestionMapper;
    }

    @GetMapping
    public ResponseEntity<List<ExamQuestionResponse>> getAllQuestions() {
        List<ExamQuestion> questions = examQuestionService.getAllExamQuestions();
        List<ExamQuestionResponse> response = questions.stream()
                .map(examQuestionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamQuestionResponse> getQuestionById(@PathVariable Long id) {
        return examQuestionService.getQuestionById(id)
                .map(question -> ResponseEntity.ok(examQuestionMapper.toResponse(question)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/random")
    public ResponseEntity<List<ExamQuestionResponse>> getRandomQuestions(
            @RequestParam(defaultValue = "50") int limit) {
        List<ExamQuestion> questions = examQuestionService.getRandomQuestions(limit);
        List<ExamQuestionResponse> response = questions.stream()
                .map(examQuestionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/random/category/{categoryId}")
    public ResponseEntity<List<ExamQuestionResponse>> getRandomQuestionsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "15") int limit) {
        List<ExamQuestion> questions = examQuestionService.getRandomQuestionsByCategory(categoryId, limit);
        List<ExamQuestionResponse> response = questions.stream()
                .map(examQuestionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
