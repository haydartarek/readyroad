package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Quiz Question Repository
 *
 * **Phase 2 Restoration:** Created January 18, 2026
 * Provides data access for quiz questions with smart query methods
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

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
        QuizQuestion.DifficultyLevel difficultyLevel
    );

    /**
     * Get random questions (MySQL RAND())
     */
    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.isActive = true ORDER BY RAND()")
    List<QuizQuestion> findRandomQuestions();

    /**
     * Get random questions with options loaded (EAGER fetch)
     * Step 1: Get random question IDs using native query
     *
     * @param limit Maximum number of questions to return
     * @return List of random question IDs
     */
    @Query(value = "SELECT q.id FROM quiz_questions q " +
                   "WHERE q.is_active = true " +
                   "ORDER BY RAND() " +
                   "LIMIT :limit",
           nativeQuery = true)
    List<Long> findRandomQuestionIds(@Param("limit") int limit);

    /**
     * Get random questions with options loaded (EAGER fetch)
     * Step 2: Fetch full questions with options by IDs
     * Uses @EntityGraph for reliable eager loading
     *
     * @param ids List of question IDs to fetch
     * @return List of questions with options eagerly loaded
     */
    @EntityGraph(attributePaths = {"options"})
    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.id IN :ids")
    List<QuizQuestion> findAllByIdWithOptions(@Param("ids") List<Long> ids);

    /**
     * Get random questions by category
     */
    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.category.id = :categoryId " +
           "AND qq.isActive = true ORDER BY RAND()")
    List<QuizQuestion> findRandomQuestionsByCategory(@Param("categoryId") Long categoryId);

    /**
     * Get random questions by category with options loaded (EAGER fetch)
     * Step 1: Get random question IDs by category using native query
     *
     * @param categoryId Category ID to filter by
     * @param limit Maximum number of questions to return
     * @return List of random question IDs from category
     */
    @Query(value = "SELECT q.id FROM quiz_questions q " +
                   "WHERE q.category_id = :categoryId AND q.is_active = true " +
                   "ORDER BY RAND() " +
                   "LIMIT :limit",
           nativeQuery = true)
    List<Long> findRandomQuestionIdsByCategory(@Param("categoryId") Long categoryId,
                                                 @Param("limit") int limit);

    /**
     * Get random questions by difficulty
     */
    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.difficultyLevel = :difficulty " +
           "AND qq.isActive = true ORDER BY RAND()")
    List<QuizQuestion> findRandomQuestionsByDifficulty(
        @Param("difficulty") QuizQuestion.DifficultyLevel difficulty
    );

    /**
     * Count active questions
     */
    Long countByIsActiveTrue();

    /**
     * Count active questions by category
     */
    Long countByCategoryIdAndIsActiveTrue(Long categoryId);

    // ========== Phase 3: Smart Quiz Methods (24h Cooldown) ==========

    /**
     * Find random questions with Pageable support (for SmartQuizService).
     * Uses JPQL ORDER BY RAND() for H2 compatibility.
     * EntityGraph: Eager-load options to prevent N+1 queries
     */
    @EntityGraph(attributePaths = {"options", "category", "trafficSign"})
    @Query("SELECT DISTINCT qq FROM QuizQuestion qq WHERE qq.isActive = true")
    List<QuizQuestion> findRandomQuestionsWithOptions(Pageable pageable);

    /**
     * Find random questions by category with Pageable support.
     * EntityGraph: Eager-load options to prevent N+1 queries
     */
    @EntityGraph(attributePaths = {"options", "category", "trafficSign"})
    @Query("SELECT DISTINCT qq FROM QuizQuestion qq WHERE qq.category.id = :categoryId " +
           "AND qq.isActive = true")
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
     * Find random questions by difficulty level with Pageable support.
     * Used for adaptive quiz generation.
     */
    @Query("SELECT qq FROM QuizQuestion qq " +
           "WHERE qq.difficultyLevel = :difficulty " +
           "AND qq.isActive = true")
    List<QuizQuestion> findByDifficultyRandom(
        @Param("difficulty") QuizQuestion.DifficultyLevel difficulty,
        Pageable pageable
    );

    /**
     * Find random questions by category AND difficulty with Pageable support.
     * Used for adaptive quiz generation with category filter.
     */
    @Query("SELECT qq FROM QuizQuestion qq " +
           "WHERE qq.category.id = :categoryId " +
           "AND qq.difficultyLevel = :difficulty " +
           "AND qq.isActive = true")
    List<QuizQuestion> findByCategoryAndDifficultyRandom(
        @Param("categoryId") Long categoryId,
        @Param("difficulty") QuizQuestion.DifficultyLevel difficulty,
        Pageable pageable
    );
}
