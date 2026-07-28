package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.custom.UserQuestionHistoryUpsertRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for tracking user question history (24h cooldown enforcement).
 */
@Repository
public interface UserQuestionHistoryRepository extends JpaRepository<UserQuestionHistory, Long>,
              UserQuestionHistoryUpsertRepository {

       /**
        * Complete cumulative question history for one user.
        * Used by the read-only student intelligence engine.
        */
       @Query("""
                     SELECT h
                     FROM UserQuestionHistory h
                     JOIN FETCH h.question q
                     JOIN FETCH q.category
                     WHERE h.userId = :userId
                     """)
       List<UserQuestionHistory> findByUserId(@Param("userId") Long userId);

       /**
        * Find all question IDs that a user has seen within a given time window.
        * Used to enforce 24h cooldown.
        *
        * @param userId User ID
        * @param since  Timestamp to check from (e.g., now minus 24 hours)
        * @return List of question IDs seen since the given timestamp
        */
       @Query("SELECT DISTINCT uqh.questionId FROM UserQuestionHistory uqh " +
                     "WHERE uqh.userId = :userId " +
                     "AND uqh.lastShownAt >= :since")
       List<Long> findRecentQuestionIdsByUserId(@Param("userId") Long userId,
                     @Param("since") LocalDateTime since);

       /**
        * Check if a specific question was shown to a user within a time window.
        *
        * @param userId     User ID
        * @param questionId Question ID
        * @param since      Timestamp to check from
        * @return true if the question was shown recently
        */
       boolean existsByUserIdAndQuestionIdAndLastShownAtAfter(Long userId,
                     Long questionId,
                     LocalDateTime since);

       /**
        * Count how many times a user has seen questions within a time window.
        *
        * @param userId User ID
        * @param since  Timestamp to check from
        * @return Count of questions seen
        */
       long countByUserIdAndLastShownAtAfter(Long userId, LocalDateTime since);

       /**
        * Find all history records for a user within a time window.
        *
        * @param userId User ID
        * @param since  Timestamp to check from
        * @return List of history records
        */
       List<UserQuestionHistory> findByUserIdAndLastShownAtAfter(Long userId, LocalDateTime since);

       /**
        * Phase 4: Find recent answered questions (where is_correct is not NULL).
        * Used for adaptive difficulty calculation.
        * Orders by answered_at DESC and limits results via Pageable.
        *
        * @param userId   User ID
        * @param since    Timestamp to check from
        * @param pageable Pageable for limiting results
        * @return List of answered questions (most recent first)
        */
       @Query("SELECT uqh FROM UserQuestionHistory uqh " +
                     "WHERE uqh.userId = :userId " +
                     "AND uqh.answeredAt >= :since " +
                     "AND uqh.isCorrect IS NOT NULL " +
                     "ORDER BY uqh.answeredAt DESC")
       List<UserQuestionHistory> findRecentAnsweredQuestions(@Param("userId") Long userId,
                     @Param("since") LocalDateTime since,
                     Pageable pageable);

       /**
        * Phase 4: Count answered questions (where is_correct is not NULL).
        * Used for statistics display.
        *
        * @param userId User ID
        * @param since  Timestamp to check from
        * @return Count of answered questions
        */
       @Query("SELECT COUNT(uqh) FROM UserQuestionHistory uqh " +
                     "WHERE uqh.userId = :userId " +
                     "AND uqh.answeredAt >= :since " +
                     "AND uqh.isCorrect IS NOT NULL")
       long countAnsweredQuestions(@Param("userId") Long userId,
                     @Param("since") LocalDateTime since);

       /**
        * Find history records for a user where answeredAt is after a given timestamp.
        * Used in integration tests and answer submission tracking.
        *
        * @param userId User ID
        * @param since  Timestamp to check from
        * @return List of history records answered after the given timestamp
        */
       List<UserQuestionHistory> findByUserIdAndAnsweredAtAfter(Long userId, LocalDateTime since);

       /**
        * Story C1: Find all wrong attempts for a user (where is_correct = false).
        * Used for error pattern analysis.
        *
        * @param userId    User ID
        * @param isCorrect Boolean value (false for wrong attempts)
        * @return List of wrong attempts
        */
       List<UserQuestionHistory> findByUserIdAndIsCorrect(Long userId, Boolean isCorrect);

       /**
        * Find history records where user answered a question incorrectly at least
        * once.
        * Uses timesIncorrect (times_wrong) which NEVER decreases, unlike is_correct
        * which
        * can be overwritten to 1 when the user later answers correctly.
        *
        * @param userId            User ID
        * @param minTimesIncorrect Minimum wrong count (use 0 to get all questions ever
        *                          wrong)
        * @return List of history rows with at least one wrong attempt
        */
       List<UserQuestionHistory> findByUserIdAndTimesIncorrectGreaterThan(Long userId, Integer minTimesIncorrect);

       /**
        * Check if any history records exist for a given question ID.
        * Used for delete/edit protection of referenced questions.
        */
       boolean existsByQuestionId(Long questionId);

       /**
        * Find distinct practice dates (YYYY-MM-DD format) for a user, descending.
        * Used to calculate real consecutive-day study streak.
        * Only considers rows where answered_at is not null (actual answers, not just
        * shown).
        *
        * @param userId User ID
        * @return List of date strings (e.g. ["2026-02-28", "2026-02-27",
        *         "2026-02-25"])
        */
       /**
        * Uses CAST(... AS DATE) for cross-database compatibility (MySQL + H2 tests).
        */
       @Query(value = "SELECT DISTINCT CAST(answered_at AS DATE) AS practice_date " +
                     "FROM user_question_history " +
                     "WHERE user_id = :userId AND answered_at IS NOT NULL " +
                     "ORDER BY practice_date DESC", nativeQuery = true)
       List<LocalDate> findDistinctAnswerDatesByUserId(@Param("userId") Long userId);

       /**
        * Find the most recent answered date (YYYY-MM-DD) for a user.
        * Returns null if user has never answered any question.
        *
        * @param userId User ID
        * @return ISO date string (yyyy-MM-dd) or null
        */
       @Query(value = "SELECT CAST(MAX(answered_at) AS DATE) " +
                     "FROM user_question_history " +
                     "WHERE user_id = :userId AND answered_at IS NOT NULL", nativeQuery = true)
       LocalDate findMostRecentAnsweredDateByUserId(@Param("userId") Long userId);

}
