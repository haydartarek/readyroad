package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findAllByUserIdOrderByStartedAtDesc(Long userId);

    List<QuizAttempt> findAllByUserIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(Long userId);

    Optional<QuizAttempt> findFirstByUserIdAndCompletedAtIsNullOrderByStartedAtDesc(Long userId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId " +
           "AND qa.completedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY qa.completedAt DESC")
    List<QuizAttempt> findUserAttemptsInDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT AVG(qa.scorePercentage) FROM QuizAttempt qa " +
           "WHERE qa.user.id = :userId AND qa.completedAt IS NOT NULL")
    Double getAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa " +
           "WHERE qa.user.id = :userId AND qa.passed = true")
    Long countPassedAttempts(@Param("userId") Long userId);
}
