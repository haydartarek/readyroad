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
}

