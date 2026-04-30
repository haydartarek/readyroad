package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignRandomPracticeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SignRandomPracticeQuestionRepository extends JpaRepository<SignRandomPracticeQuestion, Long> {

    List<SignRandomPracticeQuestion> findBySessionIdOrderByQuestionOrder(Long sessionId);

    @Query("""
            SELECT DISTINCT q.question.id
            FROM SignRandomPracticeQuestion q
            JOIN q.session s
            WHERE s.user.id = :userId
              AND s.startedAt >= :cutoff
            """)
    List<Long> findDistinctRecentQuestionIdsByUserIdSince(
            @Param("userId") Long userId,
            @Param("cutoff") LocalDateTime cutoff);
}
