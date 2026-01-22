package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Exam Simulation Answers - Phase 5
 * Updated for Story A2: Submit Exam Answer
 */
@Repository
public interface ExamSimulationAnswerRepository extends JpaRepository<ExamSimulationAnswer, Long> {

    /**
     * Find all answers for exam
     */
    List<ExamSimulationAnswer> findByExamId(Long examId);

    /**
     * Find all answers for exam ordered by submission time
     * Story A2: Submit Exam Answer
     */
    List<ExamSimulationAnswer> findByExamIdOrderByAnsweredAt(Long examId);

    /**
     * Check if answer exists for question
     */
    boolean existsByExamIdAndQuestionId(Long examId, Long questionId);

    /**
     * Find answer for specific exam and question
     * Story A2: Submit Exam Answer
     */
    Optional<ExamSimulationAnswer> findByExamIdAndQuestionId(Long examId, Long questionId);

    /**
     * Count total answers submitted in exam
     * Story A2: Submit Exam Answer
     */
    long countByExamId(Long examId);

    /**
     * Count correct answers
     */
    long countByExamIdAndIsCorrectTrue(Long examId);
}
