package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignPracticeSessionRepository extends JpaRepository<SignPracticeSession, Long> {

    /** Load a session and verify it belongs to the given user (ownership check). */
    Optional<SignPracticeSession> findByIdAndUserId(Long id, Long userId);

    /** All sessions for a user with a given status. */
    List<SignPracticeSession> findAllByUserIdAndStatus(Long userId, SessionStatus status);

    /** History: all sessions for a user, newest first. */
    List<SignPracticeSession> findAllByUserIdOrderByStartedAtDesc(Long userId);

    /** Check if user already has an in-progress session for this sign. */
    Optional<SignPracticeSession> findByUserIdAndSignIdAndStatus(Long userId, Long signId, SessionStatus status);

    /** Has the user ever started a practice session for this sign? */
    boolean existsByUserIdAndSignId(Long userId, Long signId);

    /** Has the user completed at least one practice session for this sign? */
    boolean existsByUserIdAndSignIdAndStatus(Long userId, Long signId, SessionStatus status);

    /** Count sessions for a user by status (used for dashboard summary). */
    long countByUserIdAndStatus(Long userId, SessionStatus status);

    /** Best accuracy (correctCount / totalQuestions) for user+sign, or null if none. */
    @Query(value = "SELECT MAX(correct_count / NULLIF(total_questions, 0) * 100) " +
                   "FROM sign_practice_sessions " +
                   "WHERE user_id = :userId AND sign_id = :signId AND status = 'COMPLETED'",
           nativeQuery = true)
    Double findBestScorePctByUserIdAndSignId(@Param("userId") Long userId,
                                             @Param("signId") Long signId);

    /**
     * One aggregate row per sign for the all-sign progress endpoint.
     *
     * Columns: sign_id, started_count, completed_count, best_score_pct.
     * The score expression intentionally matches
     * {@link #findBestScorePctByUserIdAndSignId(Long, Long)}.
     */
    @Query(value = """
            SELECT sign_id,
                   COUNT(*) AS started_count,
                   SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_count,
                   MAX(CASE
                           WHEN status = 'COMPLETED'
                           THEN correct_count / NULLIF(total_questions, 0) * 100
                           ELSE NULL
                       END) AS best_score_pct
            FROM sign_practice_sessions
            WHERE user_id = :userId
            GROUP BY sign_id
            """, nativeQuery = true)
    List<Object[]> findProgressSummariesByUserId(@Param("userId") Long userId);
}
