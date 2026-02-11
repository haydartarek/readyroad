package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * User Category Progress - Phase 5
 * Tracks user performance and mastery level per category.
 * 
 * **Fix Applied:** February 6, 2026
 * Changed accuracy_rate from Double to BigDecimal to match database DECIMAL
 * type
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

    /**
     * Accuracy rate stored as DECIMAL in database
     * Changed from Double to BigDecimal for precise decimal calculations
     */
    @Column(name = "accuracy_rate", precision = 5, scale = 2)
    private BigDecimal accuracyRate;

    @Column(name = "last_practiced")
    private LocalDateTime lastPracticed;

    @Enumerated(EnumType.STRING)
    @Column(name = "mastery_level", length = 20)
    private MasteryLevel masteryLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    public enum MasteryLevel {
        BEGINNER, // < 50% accuracy
        INTERMEDIATE, // 50-79% accuracy
        ADVANCED // >= 80% accuracy
    }

    /**
     * Helper method to update accuracy rate
     * Uses BigDecimal for precise calculations
     */
    public void updateAccuracy() {
        if (questionsAttempted > 0) {
            BigDecimal correct = new BigDecimal(correctAnswers);
            BigDecimal total = new BigDecimal(questionsAttempted);
            BigDecimal hundred = new BigDecimal("100.00");

            this.accuracyRate = correct
                    .multiply(hundred)
                    .divide(total, 2, RoundingMode.HALF_UP);

            updateMasteryLevel();
        }
    }

    /**
     * Update mastery level based on accuracy rate
     */
    private void updateMasteryLevel() {
        if (accuracyRate == null) {
            this.masteryLevel = MasteryLevel.BEGINNER;
        } else {
            BigDecimal eighty = new BigDecimal("80.00");
            BigDecimal fifty = new BigDecimal("50.00");

            if (accuracyRate.compareTo(eighty) >= 0) {
                this.masteryLevel = MasteryLevel.ADVANCED;
            } else if (accuracyRate.compareTo(fifty) >= 0) {
                this.masteryLevel = MasteryLevel.INTERMEDIATE;
            } else {
                this.masteryLevel = MasteryLevel.BEGINNER;
            }
        }
    }
}
