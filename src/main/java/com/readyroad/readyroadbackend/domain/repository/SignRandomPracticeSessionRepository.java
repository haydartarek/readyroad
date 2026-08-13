package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignRandomPracticeSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignRandomPracticeSessionRepository extends JpaRepository<SignRandomPracticeSession, Long> {

    Optional<SignRandomPracticeSession> findFirstByUser_IdAndStatusOrderByStartedAtDesc(
            Long userId, SignRandomPracticeSession.SessionStatus status);

    @Query("SELECT s FROM SignRandomPracticeSession s WHERE s.user.id = :userId ORDER BY s.startedAt DESC")
    List<SignRandomPracticeSession> findAllByUserIdOrderByStartedAtDesc(@Param("userId") Long userId);

    @Query("SELECT s FROM SignRandomPracticeSession s WHERE s.id = :sessionId AND s.user.id = :userId")
    Optional<SignRandomPracticeSession> findByIdAndUserId(@Param("sessionId") Long sessionId,
            @Param("userId") Long userId);

    long countByUser_Id(Long userId);

    long countByPassedTrue();

    long countByUser_IdAndStatus(Long userId, SignRandomPracticeSession.SessionStatus status);

    long countByStatus(SignRandomPracticeSession.SessionStatus status);

    @Query("SELECT COUNT(s) FROM SignRandomPracticeSession s WHERE s.user.role = 'USER' AND s.status = :status")
    long countStudentSessionsByStatus(@Param("status") SignRandomPracticeSession.SessionStatus status);

    @Query("SELECT COUNT(s) FROM SignRandomPracticeSession s WHERE s.user.role = 'USER' AND s.passed = true")
    long countPassedStudentSessions();

    @Query("SELECT COUNT(s) FROM SignRandomPracticeSession s WHERE s.user.id = :userId AND s.passed = true")
    long countByUserIdAndPassedTrue(@Param("userId") Long userId);
}
