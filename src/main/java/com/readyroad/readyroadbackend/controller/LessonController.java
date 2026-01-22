package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.dto.response.LessonResponse;
import com.readyroad.readyroadbackend.mapper.LessonMapper;
import com.readyroad.readyroadbackend.service.LessonService;
import com.readyroad.readyroadbackend.service.PracticeQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@Tag(name = "Lessons", description = "Theory lessons for driving test preparation")
public class LessonController {

    private final LessonService lessonService;
    private final PracticeQuestionService practiceQuestionService;
    private final LessonMapper lessonMapper;

    public LessonController(LessonService lessonService,
                            PracticeQuestionService practiceQuestionService,
                            LessonMapper lessonMapper) {
        this.lessonService = lessonService;
        this.practiceQuestionService = practiceQuestionService;
        this.lessonMapper = lessonMapper;
    }

    @GetMapping
    public ResponseEntity<List<LessonResponse>> getAllLessons() {
        List<Lesson> lessons = lessonService.getAllLessons();
        List<LessonResponse> response = lessons.stream()
                .map(lesson -> {
                    Long questionsCount = practiceQuestionService.getQuestionsCountByLesson(lesson.getId());
                    return lessonMapper.toResponse(lesson, questionsCount.intValue());
                })
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Long id) {
        return lessonService.getLessonById(id)
                .map(lesson -> {
                    Long questionsCount = practiceQuestionService.getQuestionsCountByLesson(lesson.getId());
                    return ResponseEntity.ok(lessonMapper.toResponse(lesson, questionsCount.intValue()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<LessonResponse>> getLessonsByCategory(@PathVariable Long categoryId) {
        List<Lesson> lessons = lessonService.getLessonsByCategory(categoryId);
        List<LessonResponse> response = lessons.stream()
                .map(lesson -> {
                    Long questionsCount = practiceQuestionService.getQuestionsCountByLesson(lesson.getId());
                    return lessonMapper.toResponse(lesson, questionsCount.intValue());
                })
                .toList();
        return ResponseEntity.ok(response);
    }
}
