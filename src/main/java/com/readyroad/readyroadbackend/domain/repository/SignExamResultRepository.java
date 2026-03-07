package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignExamResultRepository extends JpaRepository<SignExamResult, Long> {

    /** Check if user has passed exam 1 for a given sign (used to unlock exam 2). */
    boolean existsByUserIdAndSignCodeAndExamNumberAndPassedTrue(
            Long userId, String signCode, Integer examNumber);

    /** Has user ever attempted a specific exam for this sign? */
    boolean existsByUserIdAndSignCodeAndExamNumber(Long userId, String signCode, Integer examNumber);

    /** Get all results for a specific user+sign+exam combo, best score first. */
    @Query("SELECT r FROM SignExamResult r " +
           "WHERE r.userId = :userId AND r.signCode = :signCode AND r.examNumber = :examNumber " +
           "ORDER BY r.scorePct DESC, r.completedAt DESC")
    List<SignExamResult> findByUserIdAndSignCodeAndExamNumber(
            @Param("userId") Long userId,
            @Param("signCode") String signCode,
            @Param("examNumber") Integer examNumber);

    /** Return the latest attempt for a user+sign+exam. */
    Optional<SignExamResult> findTopByUserIdAndSignCodeAndExamNumberOrderByCompletedAtDesc(
            Long userId, String signCode, Integer examNumber);

    /** Return all results for a user on a specific sign (both exams). */
    List<SignExamResult> findByUserIdAndSignCodeOrderByCompletedAtDesc(
            Long userId, String signCode);

    /** Get best score for a user+sign+exam. */
    @Query("SELECT MAX(r.scorePct) FROM SignExamResult r " +
           "WHERE r.userId = :userId AND r.signCode = :signCode AND r.examNumber = :examNumber")
    Double findBestScorePctByUserIdAndSignCodeAndExamNumber(
            @Param("userId") Long userId,
            @Param("signCode") String signCode,
            @Param("examNumber") Integer examNumber);

    /** Count attempts for a user+sign+exam. */
    long countByUserIdAndSignCodeAndExamNumber(Long userId, String signCode, Integer examNumber);

    /** Summary: how many signs the user has passed exam 1 for. */
    @Query("SELECT COUNT(DISTINCT r.signCode) FROM SignExamResult r " +
           "WHERE r.userId = :userId AND r.examNumber = 1 AND r.passed = true")
    long countDistinctSignsWithPassedExam1(@Param("userId") Long userId);

    /** Total sign exam submissions for a user (all exam numbers). */
    long countByUserId(Long userId);
}

