package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User Category Progress - Phase 5
 */
@Repository
public interface UserCategoryProgressRepository extends JpaRepository<UserCategoryProgress, Long> {

    /**
     * Find progress for specific user and category
     */
    Optional<UserCategoryProgress> findByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Find all progress entries for user (any order)
     * Eagerly loads category relationship for analytics
     */
    @Query("SELECT p FROM UserCategoryProgress p LEFT JOIN FETCH p.category WHERE p.userId = :userId")
    List<UserCategoryProgress> findByUserId(@Param("userId") Long userId);

    /**
     * Find all progress entries for user ordered by accuracy
     */
    List<UserCategoryProgress> findByUserIdOrderByAccuracyRateAsc(Long userId);

    /**
     * Find weak categories (accuracy < 70%)
     */
    @Query("SELECT p FROM UserCategoryProgress p WHERE p.userId = :userId AND p.accuracyRate < 70.0 ORDER BY p.accuracyRate ASC")
    List<UserCategoryProgress> findWeakCategoriesByUserId(Long userId);

    /**
     * Find strong categories (accuracy >= 85%)
     */
    @Query("SELECT p FROM UserCategoryProgress p WHERE p.userId = :userId AND p.accuracyRate >= 85.0 ORDER BY p.accuracyRate DESC")
    List<UserCategoryProgress> findStrongCategoriesByUserId(Long userId);

    /**
     * Global category performance stats aggregated across all users — single SQL
     * query
     * replaces the previous full table scan + in-memory GROUP BY.
     * Returns rows: [category_id, code, name_en, total_attempted, total_correct,
     * user_count, avg_accuracy]
     */
    @Query(value = "SELECT p.category_id, c.code, c.name_en, " +
            "       SUM(p.questions_attempted) AS total_attempted, " +
            "       SUM(p.correct_answers) AS total_correct, " +
            "       COUNT(p.id) AS user_count, " +
            "       AVG(p.accuracy_rate) AS avg_accuracy " +
            "FROM user_category_progress p " +
            "JOIN categories c ON c.id = p.category_id " +
            "JOIN users u ON u.id = p.user_id AND u.role = 'USER' " +
            "GROUP BY p.category_id, c.code, c.name_en " +
            "ORDER BY avg_accuracy ASC", nativeQuery = true)
    List<Object[]> findCategoryStatsAggregated();
}
