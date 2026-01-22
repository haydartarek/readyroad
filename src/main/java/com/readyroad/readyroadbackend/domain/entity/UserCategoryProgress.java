package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Category Progress - Phase 5
 * Tracks user performance and mastery level per category.
 */
@Entity
@Table(name = "user_category_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserCategoryProgress extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "questions_attempted", nullable = false)
    private Integer questionsAttempted = 0;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers = 0;

    @Column(name = "accuracy_rate")
    private Double accuracyRate;

    @Column(name = "last_practiced")
    private LocalDateTime lastPracticed;

    @Enumerated(EnumType.STRING)
    @Column(name = "mastery_level", length = 20)
    private MasteryLevel masteryLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    public enum MasteryLevel {
        BEGINNER,      // < 50% accuracy
        INTERMEDIATE,  // 50-79% accuracy
        ADVANCED       // >= 80% accuracy
    }

    // Helper methods
    public void updateAccuracy() {
        if (questionsAttempted > 0) {
            this.accuracyRate = (correctAnswers * 100.0) / questionsAttempted;
            updateMasteryLevel();
        }
    }

    private void updateMasteryLevel() {
        if (accuracyRate == null) {
            this.masteryLevel = MasteryLevel.BEGINNER;
        } else if (accuracyRate >= 80.0) {
            this.masteryLevel = MasteryLevel.ADVANCED;
        } else if (accuracyRate >= 50.0) {
            this.masteryLevel = MasteryLevel.INTERMEDIATE;
        } else {
            this.masteryLevel = MasteryLevel.BEGINNER;
        }
    }
}
