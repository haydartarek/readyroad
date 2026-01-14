package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.PracticeQuestion;
import com.readyroad.readyroadbackend.dto.response.PracticeQuestionResponse;
import com.readyroad.readyroadbackend.mapper.PracticeQuestionMapper;
import com.readyroad.readyroadbackend.service.PracticeQuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practice-questions")
public class PracticeQuestionController {

    private final PracticeQuestionService practiceQuestionService;
    private final PracticeQuestionMapper practiceQuestionMapper;

    public PracticeQuestionController(PracticeQuestionService practiceQuestionService,
                                     PracticeQuestionMapper practiceQuestionMapper) {
        this.practiceQuestionService = practiceQuestionService;
        this.practiceQuestionMapper = practiceQuestionMapper;
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<PracticeQuestionResponse>> getQuestionsByLesson(@PathVariable Long lessonId) {
        List<PracticeQuestion> questions = practiceQuestionService.getQuestionsByLesson(lessonId);
        List<PracticeQuestionResponse> response = questions.stream()
                .map(practiceQuestionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PracticeQuestionResponse> getQuestionById(@PathVariable Long id) {
        return practiceQuestionService.getQuestionById(id)
                .map(question -> ResponseEntity.ok(practiceQuestionMapper.toResponse(question)))
                .orElse(ResponseEntity.notFound().build());
    }
}
