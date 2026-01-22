package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
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
        ExamSimulation.ExamStatus status
    );

    /**
     * Find all exams for user (for history)
     */
    List<ExamSimulation> findByUserIdOrderByStartedAtDesc(Long userId);

    /**
     * Check if user has active exam
     */
    boolean existsByUserIdAndStatus(Long userId, ExamSimulation.ExamStatus status);

    /**
     * Count completed exams for user
     */
    long countByUserIdAndStatus(Long userId, ExamSimulation.ExamStatus status);
}
