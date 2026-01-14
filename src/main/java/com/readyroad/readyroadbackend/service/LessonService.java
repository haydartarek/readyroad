package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    public List<Lesson> getLessonsByCategory(Long categoryId) {
        return lessonRepository.findByCategoryIdOrderByDisplayOrderAsc(categoryId);
    }

    public Long getTotalLessonsCount() {
        return lessonRepository.count();
    }
}
