package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.custom.QuizQuestionRandomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;

/**
 * Quiz Question Repository
 *
 * **Phase 2 Restoration:** Created January 18, 2026
 * Provides data access for quiz questions with smart query methods
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long>, QuizQuestionRandomRepository {

        /**
         * Find all active quiz questions
         */
        List<QuizQuestion> findByIsActiveTrue();

        /**
         * Find questions by category
         */
        List<QuizQuestion> findByCategoryIdAndIsActiveTrue(Long categoryId);

        /**
         * Find questions by difficulty level
         */
        List<QuizQuestion> findByDifficultyLevelAndIsActiveTrue(QuizQuestion.DifficultyLevel difficultyLevel);

        /**
         * Find questions by category and difficulty
         */
        List<QuizQuestion> findByCategoryIdAndDifficultyLevelAndIsActiveTrue(
                        Long categoryId,
                        QuizQuestion.DifficultyLevel difficultyLevel);

        /**
         * Fetch questions with every association required by response and result
         * mapping. Used by all ID-based quiz delivery paths so mapping remains safe
         * after the service transaction closes.
         *
         * @param ids List of question IDs
         * @return Questions with options and category loaded
         */
        @EntityGraph(attributePaths = { "options", "category" })
        @Query("SELECT qq FROM QuizQuestion qq WHERE qq.id IN :ids")
        List<QuizQuestion> findAllByIdWithOptionsAndCategory(@Param("ids") List<Long> ids);

        /**
         * Count active questions
         */
        Long countByIsActiveTrue();

        /**
         * Count active + PUBLISHED questions (for delivery stats)
         */
        Long countByIsActiveTrueAndStatus(QuizQuestion.QuestionStatus status);

        /** Count questions eligible for delivery for one difficulty tier. */
        @Query(value = "SELECT COUNT(*) FROM (" +
                        "  SELECT q.id FROM quiz_questions q" +
                        "  JOIN quiz_answer_options o ON o.question_id = q.id AND o.is_active = true" +
                        "  WHERE q.is_active = true AND q.status = 'PUBLISHED'" +
                        "  AND q.difficulty_level = :difficulty" +
                        "  GROUP BY q.id, q.difficulty_level" +
                        "  HAVING SUM(CASE WHEN o.is_correct = true THEN 1 ELSE 0 END) = 1" +
                        "  AND ((q.difficulty_level = 'HARD' AND COUNT(o.id) = 2)" +
                        "    OR (q.difficulty_level IN ('EASY', 'MEDIUM') AND COUNT(o.id) = 3))" +
                        ") eligible", nativeQuery = true)
        long countEligibleQuestionsByDifficulty(@Param("difficulty") String difficulty);

        /**
         * Count active questions by category
         */
        Long countByCategoryIdAndIsActiveTrue(Long categoryId);

        /**
         * Count active + PUBLISHED questions by category (for delivery stats)
         */
        Long countByCategoryIdAndIsActiveTrueAndStatus(Long categoryId, QuizQuestion.QuestionStatus status);

        // ========== Paged Delivery Methods (cooldown-aware generation) ==========

        /**
         * Find random PUBLISHED questions with Pageable support for active quiz
         * generation.
         * Filters: isActive=true AND status=PUBLISHED (Belgian compliance).
         * EntityGraph: Eager-load options to prevent N+1 queries
         */
        @EntityGraph(attributePaths = { "options", "category" })
        @Query("SELECT DISTINCT qq FROM QuizQuestion qq WHERE qq.isActive = true AND qq.status = 'PUBLISHED'")
        List<QuizQuestion> findRandomQuestionsWithOptions(Pageable pageable);

        /**
         * Find random PUBLISHED questions by category with Pageable support.
         * Filters: isActive=true AND status=PUBLISHED (Belgian compliance).
         * EntityGraph: Eager-load options to prevent N+1 queries
         */
        @EntityGraph(attributePaths = { "options", "category" })
        @Query("SELECT DISTINCT qq FROM QuizQuestion qq WHERE qq.category.id = :categoryId " +
                        "AND qq.isActive = true AND qq.status = 'PUBLISHED'")
        List<QuizQuestion> findRandomQuestionsByCategoryWithOptions(
                        @Param("categoryId") Long categoryId,
                        Pageable pageable);

        /**
         * Count questions in a list of IDs that belong to a specific category.
         * Used for calculating fresh question count per category.
         */
        long countByIdInAndCategoryId(List<Long> ids, Long categoryId);

        // ========== Phase 4: Adaptive Difficulty Methods (Law #2) ==========

        /**
         * Find random PUBLISHED questions by difficulty level with Pageable support.
         * Filters: isActive=true AND status=PUBLISHED.
         */
        @Query("SELECT qq FROM QuizQuestion qq " +
                        "WHERE qq.difficultyLevel = :difficulty " +
                        "AND qq.isActive = true AND qq.status = 'PUBLISHED'")
        List<QuizQuestion> findByDifficultyRandom(
                        @Param("difficulty") QuizQuestion.DifficultyLevel difficulty,
                        Pageable pageable);

        /**
         * Find random PUBLISHED questions by category AND difficulty with Pageable
         * support.
         * Filters: isActive=true AND status=PUBLISHED.
         */
        @Query("SELECT qq FROM QuizQuestion qq " +
                        "WHERE qq.category.id = :categoryId " +
                        "AND qq.difficultyLevel = :difficulty " +
                        "AND qq.isActive = true AND qq.status = 'PUBLISHED'")
        List<QuizQuestion> findByCategoryAndDifficultyRandom(
                        @Param("categoryId") Long categoryId,
                        @Param("difficulty") QuizQuestion.DifficultyLevel difficulty,
                        Pageable pageable);

        // ========== Admin CRUD Methods ==========

        /**
         * Admin paginated search with optional filters.
         * Mirrors the admin sign pagination pattern.
         */
        @EntityGraph(attributePaths = { "options", "category" })
        @Query("SELECT qq FROM QuizQuestion qq JOIN qq.category c WHERE " +
                        "(:categoryCode IS NULL OR c.code = :categoryCode) AND " +
                        "(:difficulty IS NULL OR qq.difficultyLevel = :difficulty) AND " +
                        "(:hasImage IS NULL OR " +
                        " (:hasImage = true AND LENGTH(TRIM(COALESCE(qq.contentImageUrl, ''))) > 0) OR " +
                        " (:hasImage = false AND LENGTH(TRIM(COALESCE(qq.contentImageUrl, ''))) = 0)) AND " +
                        "(:q IS NULL OR :q = '' OR " +
                        " LOWER(qq.questionEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                        " LOWER(qq.questionAr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                        " LOWER(qq.questionNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                        " LOWER(qq.questionFr) LIKE LOWER(CONCAT('%', :q, '%')))")
        Page<QuizQuestion> findAdminQuestions(
                        @Param("categoryCode") String categoryCode,
                        @Param("difficulty") QuizQuestion.DifficultyLevel difficulty,
                        @Param("hasImage") Boolean hasImage,
                        @Param("q") String q,
                        Pageable pageable);

        /**
         * Find a single question with options eagerly loaded (for admin detail view).
         */
        @EntityGraph(attributePaths = { "options", "category" })
        @Query("SELECT qq FROM QuizQuestion qq WHERE qq.id = :id")
        Optional<QuizQuestion> findByIdWithOptions(@Param("id") Long id);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT qq FROM QuizQuestion qq WHERE qq.id = :id")
        Optional<QuizQuestion> findByIdForUpdate(@Param("id") Long id);

        /** Prevent duplicate theory questions during admin JSON imports. */
        @Query("SELECT CASE WHEN COUNT(qq) > 0 THEN true ELSE false END FROM QuizQuestion qq " +
                        "WHERE qq.category.id = :categoryId " +
                        "AND LOWER(TRIM(qq.questionEn)) = LOWER(TRIM(:questionEn))")
        boolean existsByCategoryIdAndNormalizedQuestionEn(
                        @Param("categoryId") Long categoryId,
                        @Param("questionEn") String questionEn);

        // ========== Integrity Diagnostic Counts (Admin diagnostics endpoint)
        // ==========

        /** Questions with fewer than 2 options (Belgian minimum). */
        @Query(value = "SELECT COUNT(*) FROM (" +
                        "  SELECT q.id FROM quiz_questions q" +
                        "  LEFT JOIN quiz_answer_options o ON o.question_id = q.id AND o.is_active = true" +
                        "  GROUP BY q.id HAVING COUNT(o.id) < 2" +
                        ") subq", nativeQuery = true)
        long countQuestionsWithFewerThanTwoOptions();

        /** Questions with more than 3 options (Belgian maximum). */
        @Query(value = "SELECT COUNT(*) FROM (" +
                        "  SELECT q.id FROM quiz_questions q" +
                        "  LEFT JOIN quiz_answer_options o ON o.question_id = q.id AND o.is_active = true" +
                        "  GROUP BY q.id HAVING COUNT(o.id) > 3" +
                        ") subq", nativeQuery = true)
        long countQuestionsWithMoreThanThreeOptions();

        /** Questions that have no correct answer option. */
        @Query(value = "SELECT COUNT(*) FROM (" +
                        "  SELECT q.id FROM quiz_questions q" +
                        "  LEFT JOIN quiz_answer_options o ON o.question_id = q.id AND o.is_active = true AND o.is_correct = true" +
                        "  GROUP BY q.id HAVING COUNT(o.id) = 0" +
                        ") subq", nativeQuery = true)
        long countQuestionsWithZeroCorrectOptions();

        /** Questions that have more than one correct answer option. */
        @Query(value = "SELECT COUNT(*) FROM (" +
                        "  SELECT q.id FROM quiz_questions q" +
                        "  JOIN quiz_answer_options o ON o.question_id = q.id AND o.is_active = true AND o.is_correct = true" +
                        "  GROUP BY q.id HAVING COUNT(o.id) > 1" +
                        ") subq", nativeQuery = true)
        long countQuestionsWithMultipleCorrectOptions();

        /** Questions that have at least one option with blank/null English text. */
        @Query(value = "SELECT COUNT(DISTINCT q.id) FROM quiz_questions q" +
                        " JOIN quiz_answer_options o ON o.question_id = q.id AND o.is_active = true" +
                        " WHERE o.option_text_en IS NULL OR TRIM(o.option_text_en) = ''", nativeQuery = true)
        long countQuestionsWithOptionsMissingEnglishText();

        /** Questions that are inactive but still have PUBLISHED status (anomaly). */
        @Query(value = "SELECT COUNT(*) FROM quiz_questions WHERE is_active = false AND status = 'PUBLISHED'", nativeQuery = true)
        long countInactivePublishedQuestions();

        /** Questions that are active but still in DRAFT status (anomaly). */
        @Query(value = "SELECT COUNT(*) FROM quiz_questions WHERE is_active = true AND status = 'DRAFT'", nativeQuery = true)
        long countActiveDraftQuestions();

        // ========== Compliant Question Counts (Stats Accuracy) ==========

        /**
         * Count questions that are delivery-compliant:
         * isActive=true, status=PUBLISHED, 2-3 options, exactly 1 correct.
         * Ensures stats match the actual deliverable pool.
         */
        @Query(value = "SELECT COUNT(*) FROM (" +
                        "  SELECT q.id FROM quiz_questions q" +
                        "  JOIN quiz_answer_options o ON o.question_id = q.id AND o.is_active = true" +
                        "  WHERE q.is_active = true AND q.status = 'PUBLISHED'" +
                        "  GROUP BY q.id" +
                        "  HAVING COUNT(o.id) BETWEEN 2 AND 3" +
                        "  AND SUM(CASE WHEN o.is_correct = true THEN 1 ELSE 0 END) = 1" +
                        ") compliant", nativeQuery = true)
        long countCompliantQuestions();

        /**
         * Count delivery-compliant questions in a specific category.
         */
        @Query(value = "SELECT COUNT(*) FROM (" +
                        "  SELECT q.id FROM quiz_questions q" +
                        "  JOIN quiz_answer_options o ON o.question_id = q.id AND o.is_active = true" +
                        "  WHERE q.is_active = true AND q.status = 'PUBLISHED'" +
                        "  AND q.category_id = :categoryId" +
                        "  GROUP BY q.id" +
                        "  HAVING COUNT(o.id) BETWEEN 2 AND 3" +
                        "  AND SUM(CASE WHEN o.is_correct = true THEN 1 ELSE 0 END) = 1" +
                        ") compliant", nativeQuery = true)
        long countCompliantQuestionsByCategory(@Param("categoryId") Long categoryId);

        /**
         * Batch compliant counts per category — replaces N+1 per-category calls in getCategoryProgress().
         * Returns [category_id, count] rows for all categories that have at least one compliant question.
         */
        @Query(value = "SELECT compliant.category_id, COUNT(*) AS cnt FROM (" +
                        "  SELECT q.id, q.category_id FROM quiz_questions q" +
                        "  JOIN quiz_answer_options o ON o.question_id = q.id AND o.is_active = true" +
                        "  WHERE q.is_active = true AND q.status = 'PUBLISHED'" +
                        "  GROUP BY q.id, q.category_id" +
                        "  HAVING COUNT(o.id) BETWEEN 2 AND 3" +
                        "  AND SUM(CASE WHEN o.is_correct = true THEN 1 ELSE 0 END) = 1" +
                        ") compliant GROUP BY compliant.category_id", nativeQuery = true)
        List<Object[]> countCompliantQuestionsByCategoryIds();
}
