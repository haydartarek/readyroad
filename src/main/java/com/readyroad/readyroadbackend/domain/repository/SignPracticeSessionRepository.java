package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
