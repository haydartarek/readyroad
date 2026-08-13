package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Exam Simulation - Phase 5
 */
@Repository
public interface ExamSimulationRepository extends JpaRepository<ExamSimulation, Long> {

    /**
     * Find active exam for user
     */
    Optional<ExamSimulation> findByUserIdAndStatus(Long userId, ExamSimulation.ExamStatus status);

    /**
     * Find all completed exams for user
     */
    List<ExamSimulation> findByUserIdAndStatusOrderByCompletedAtDesc(
            Long userId,
            ExamSimulation.ExamStatus status);

    /** All statuses for operational activity counters; never use as result history. */
    List<ExamSimulation> findByUserIdOrderByStartedAtDesc(Long userId);

    /**
     * Check if user has active exam
     */
    boolean existsByUserIdAndStatus(Long userId, ExamSimulation.ExamStatus status);

    /**
     * Count completed exams for user
     */
    long countByUserIdAndStatus(Long userId, ExamSimulation.ExamStatus status);

    /**
     * Count completed exams where user achieved a passing score (≥ passThreshold)
     */
    long countByUserIdAndStatusAndScorePercentageGreaterThanEqual(
            Long userId,
            ExamSimulation.ExamStatus status,
            double passThreshold);

    /**
     * Find all exams with given status ordered by completion date (admin use)
     */
    List<ExamSimulation> findByStatusOrderByCompletedAtDesc(
            ExamSimulation.ExamStatus status,
            Pageable pageable);

    @Query("""
            SELECT es FROM ExamSimulation es, User u
            WHERE es.userId = u.id AND u.role = 'USER' AND es.status = :status
            ORDER BY es.completedAt DESC
            """)
    List<ExamSimulation> findStudentExamsByStatusOrderByCompletedAtDesc(
            ExamSimulation.ExamStatus status, Pageable pageable);

    /**
     * Count all exams with given status (admin use)
     */
    long countByStatus(ExamSimulation.ExamStatus status);

    /**
     * Average score for all completed exams (admin analytics)
     */
    @Query("SELECT AVG(es.scorePercentage) FROM ExamSimulation es WHERE es.status = 'COMPLETED'")
    Double getAverageScoreOfCompleted();

    @Query("""
            SELECT AVG(es.scorePercentage) FROM ExamSimulation es, User u
            WHERE es.userId = u.id AND u.role = 'USER' AND es.status = 'COMPLETED'
            """)
    Double getStudentAverageScoreOfCompleted();

    @Query("""
            SELECT COUNT(es) FROM ExamSimulation es, User u
            WHERE es.userId = u.id AND u.role = 'USER' AND es.status = :status
            """)
    long countStudentExamsByStatus(ExamSimulation.ExamStatus status);

    @Query("""
            SELECT COUNT(es) FROM ExamSimulation es, User u
            WHERE es.userId = u.id AND u.role = 'USER' AND es.status = :status
              AND es.correctAnswers >= :threshold
            """)
    long countStudentExamsByStatusAndCorrectAnswersGreaterThanEqual(
            ExamSimulation.ExamStatus status, Integer threshold);

    /**
     * Count completed exams where user passed (correctAnswers >= threshold)
     */
    long countByStatusAndCorrectAnswersGreaterThanEqual(ExamSimulation.ExamStatus status, Integer threshold);
}
