package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonImportServiceIdempotencyTest {

    @Mock
    private LessonRepository lessonRepository;

    private final Map<String, Lesson> storedLessons = new LinkedHashMap<>();
    private LessonImportService lessonImportService;

    @BeforeEach
    void setUp() {
        lessonImportService = new LessonImportService(lessonRepository, new ObjectMapper());
        when(lessonRepository.findByLessonCode(any())).thenAnswer(invocation ->
                Optional.ofNullable(storedLessons.get(invocation.getArgument(0))));
        when(lessonRepository.saveAndFlush(any(Lesson.class))).thenAnswer(invocation -> {
            Lesson lesson = invocation.getArgument(0);
            storedLessons.put(lesson.getLessonCode(), lesson);
            return lesson;
        });
    }

    @Test
    void identicalCanonicalImportIsSkippedWithoutRebuildingLessons() {
        var firstImport = lessonImportService.importFromClasspath();
        var secondImport = lessonImportService.importFromClasspath();

        assertThat(firstImport.created()).isEqualTo(30);
        assertThat(secondImport.created()).isZero();
        assertThat(secondImport.updated()).isZero();
        assertThat(secondImport.skipped()).isEqualTo(30);
        assertThat(secondImport.errors()).isEmpty();
    }
}
