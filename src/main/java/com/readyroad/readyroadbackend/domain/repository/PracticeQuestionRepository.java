package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.PracticeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeQuestionRepository extends JpaRepository<PracticeQuestion, Long> {
    List<PracticeQuestion> findByLessonIdAndIsActiveTrue(Long lessonId);
    List<PracticeQuestion> findByLessonIdOrderByDisplayOrderAsc(Long lessonId);
    Long countByLessonId(Long lessonId);
}
