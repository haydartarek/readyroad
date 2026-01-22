package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCategoryIdAndIsActiveTrue(Long categoryId);
    List<Lesson> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<Lesson> findByCategoryIdOrderByDisplayOrderAsc(Long categoryId);
}
