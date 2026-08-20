package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    /** Check whether a theory question is part of any persisted exam. */
    boolean existsByQuestionId(Long questionId);

    /** Atomically records the first real presentation within one persisted exam. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE exam_simulation_questions
            SET presented_at = :presentedAt,
                updated_at = :presentedAt
            WHERE exam_id = :examId
              AND question_id = :questionId
              AND presented_at IS NULL
            """, nativeQuery = true)
    int markPresentedIfAbsent(
            @Param("examId") Long examId,
            @Param("questionId") Long questionId,
            @Param("presentedAt") LocalDateTime presentedAt);
}
