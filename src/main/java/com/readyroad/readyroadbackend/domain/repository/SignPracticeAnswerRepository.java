package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignPracticeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignPracticeAnswerRepository extends JpaRepository<SignPracticeAnswer, Long> {

    /** True if the user already answered this question in the given session. */
    boolean existsBySessionIdAndQuestionId(Long sessionId, Long questionId);

    /** All answers for a session, loaded with question + choice for DTO mapping. */
    @Query("""
            SELECT a FROM SignPracticeAnswer a
            JOIN FETCH a.question q
            JOIN FETCH a.choice   c
            WHERE a.session.id = :sessionId
            ORDER BY a.answeredAt ASC
            """)
    List<SignPracticeAnswer> findAllBySessionIdWithDetails(@Param("sessionId") Long sessionId);

    /** IDs of questions that have already been answered in the session. */
    @Query("""
            SELECT a.question.id FROM SignPracticeAnswer a
            WHERE a.session.id = :sessionId
            ORDER BY a.answeredAt ASC
            """)
    List<Long> findQuestionIdsBySessionId(@Param("sessionId") Long sessionId);

    /** Count of answers already submitted for a session (to detect completion). */
    long countBySessionId(Long sessionId);
}
