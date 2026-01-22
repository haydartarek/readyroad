package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Exam Simulation Questions - Phase 5
 * Updated for Story A2: Submit Exam Answer
 */
@Repository
public interface ExamSimulationQuestionRepository extends JpaRepository<ExamSimulationQuestion, Long> {

    /**
     * Find all questions for exam in order
     */
    List<ExamSimulationQuestion> findByExamIdOrderByQuestionOrder(Long examId);

    /**
     * Find specific question in exam
     * Story A2: Submit Exam Answer
     */
    Optional<ExamSimulationQuestion> findByExamIdAndQuestionId(Long examId, Long questionId);

    /**
     * Count questions in exam
     */
    long countByExamId(Long examId);
}
