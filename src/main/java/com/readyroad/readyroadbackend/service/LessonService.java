package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.dto.response.LessonDetailResponse;
import com.readyroad.readyroadbackend.dto.response.LessonResponse;
import com.readyroad.readyroadbackend.mapper.LessonMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    public LessonService(LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }

    /**
     * All active lessons ordered by displayOrder (summary only, no pages).
     */
    public List<LessonResponse> getAllActiveLessons() {
        return lessonRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(lessonMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Single lesson with all pages.
     *
     * @param idOrCode numeric DB id as string OR lessonCode like "les-0"
     */
    public LessonDetailResponse getLessonByIdOrCode(String idOrCode) {
        Lesson lesson;
        try {
            Long id = Long.parseLong(idOrCode);
            lesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + idOrCode));
        } catch (NumberFormatException e) {
            lesson = lessonRepository.findByLessonCode(idOrCode)
                    .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + idOrCode));
        }
        return lessonMapper.toDetailResponse(lesson);
    }

    /**
     * Search active lessons by query string.
     */
    public List<LessonResponse> searchLessons(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllActiveLessons();
        }
        return lessonRepository.searchLessons(query.trim())
                .stream()
                .map(lessonMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Total number of active lessons.
     */
    public long countActiveLessons() {
        return lessonRepository.countByIsActiveTrue();
    }
}
