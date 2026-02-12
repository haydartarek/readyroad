package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import org.springframework.data.jpa.repository.EntityGraph;
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
     * Global category stats aggregated across all users
     */
    @Query("SELECT p FROM UserCategoryProgress p LEFT JOIN FETCH p.category ORDER BY p.categoryId")
    List<UserCategoryProgress> findAllWithCategory();
}
