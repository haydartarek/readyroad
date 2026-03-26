package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignExamResultRepository extends JpaRepository<SignExamResult, Long> {

        /** Has the user ever attempted the exam for this sign? */
        boolean existsByUserIdAndSignCode(Long userId, String signCode);

        /** Has the user passed the exam for this sign at least once? */
        boolean existsByUserIdAndSignCodeAndPassedTrue(Long userId, String signCode);

        /** Return all results for a user on a specific sign, latest first. */
        List<SignExamResult> findByUserIdAndSignCodeOrderByCompletedAtDesc(
                        Long userId, String signCode);

        /** Get the best score for a user+sign. */
        @Query("SELECT MAX(r.scorePct) FROM SignExamResult r " +
                        "WHERE r.userId = :userId AND r.signCode = :signCode")
        Double findBestScorePctByUserIdAndSignCode(
                        @Param("userId") Long userId,
                        @Param("signCode") String signCode);

        /** Count all attempts for a user+sign. */
        long countByUserIdAndSignCode(Long userId, String signCode);

        /** How many distinct signs the user has passed the exam for. */
        @Query("SELECT COUNT(DISTINCT r.signCode) FROM SignExamResult r " +
                        "WHERE r.userId = :userId AND r.passed = true")
        long countDistinctSignsWithPassedExam(@Param("userId") Long userId);

        /** Total sign exam submissions for a user. */
        long countByUserId(Long userId);
}
