package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
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
     * Complete official-exam answer history with mapper dependencies loaded.
     * Used for category evolution without changing the persistence model.
     */
    @Query("""
            SELECT a
            FROM ExamSimulationAnswer a
            JOIN FETCH a.exam e
            JOIN FETCH a.question q
            JOIN FETCH q.category c
            WHERE e.userId = :userId
              AND e.status = :status
              AND a.answerState = com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer.AnswerState.ANSWERED
            ORDER BY a.answeredAt DESC
            """)
    List<ExamSimulationAnswer> findHistoryForUser(
            @Param("userId") Long userId,
            @Param("status") ExamSimulation.ExamStatus status);

    @Query("""
            SELECT COUNT(a)
            FROM ExamSimulationAnswer a
            JOIN a.exam e
            WHERE e.userId = :userId
              AND e.status = com.readyroad.readyroadbackend.domain.entity.ExamSimulation.ExamStatus.COMPLETED
              AND a.answerState = com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer.AnswerState.TIMED_OUT
            """)
    long countCompletedTheoryTimeouts(@Param("userId") Long userId);

    @Query("""
            SELECT a
            FROM ExamSimulationAnswer a
            JOIN FETCH a.exam e
            JOIN FETCH a.question q
            LEFT JOIN FETCH q.category
            WHERE e.userId = :userId
              AND e.status = com.readyroad.readyroadbackend.domain.entity.ExamSimulation.ExamStatus.COMPLETED
              AND a.answerState = com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer.AnswerState.TIMED_OUT
            ORDER BY a.timedOutAt DESC, a.id DESC
            """)
    List<ExamSimulationAnswer> findRecentCompletedTheoryTimeouts(
            @Param("userId") Long userId,
            Pageable pageable);

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
