package com.readyroad.readyroadbackend.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Category Breakdown DTO - Story A3 (Production Ready)
 *
 * Shows performance per category in the exam
 * Version: 2.0 - Enhanced with performance levels and weak area detection
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBreakdownDTO {

    private Long categoryId;

    // Category Names (4 languages)
    private String categoryNameEn;
    private String categoryNameAr;
    private String categoryNameNl;
    private String categoryNameFr;

    // Statistics
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Double accuracyPercentage;

    // ========== PRODUCTION ENHANCEMENTS (v2.0) ==========

    // Quick Reference
    private String categoryCode; // "A", "B", "C" for easy identification

    // Performance Level: "EXCELLENT" (≥80%), "GOOD" (≥60%), "NEEDS_IMPROVEMENT" (<60%)
    private String performanceLevel;

    // Weak Area Flag: true if accuracy < 60%
    private Boolean isWeakArea;
}
