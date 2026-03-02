package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.Achievement;
import com.readyroad.readyroadbackend.domain.entity.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Achievement entities.
 * The DB UNIQUE constraint on (user_id, type) prevents duplicate awards.
 */
@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    /**
     * Check whether a user already has a specific achievement.
     * Used before awarding to prevent duplicates.
     */
    boolean existsByUserIdAndType(Long userId, AchievementType type);

    /**
     * Find a specific achievement for a user (if it exists).
     */
    Optional<Achievement> findByUserIdAndType(Long userId, AchievementType type);

    /**
     * Count how many achievements a user has earned.
     */
    long countByUserId(Long userId);
}
