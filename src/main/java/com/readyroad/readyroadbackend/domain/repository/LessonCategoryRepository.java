package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.LessonCategory;
import com.readyroad.readyroadbackend.domain.entity.LessonCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonCategoryRepository
        extends JpaRepository<LessonCategory, LessonCategoryId> {

    List<LessonCategory> findAllByLesson_IdOrderByDisplayOrderAsc(Long lessonId);

    Optional<LessonCategory> findByLesson_IdAndPrimaryTrue(Long lessonId);

    void deleteAllByLesson_Id(Long lessonId);

    long countByLesson_Id(Long lessonId);
}