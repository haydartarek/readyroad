package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.util.TextNormalizer;
import com.readyroad.readyroadbackend.validation.BelgianOptionsCount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Quiz Question Entity
 * Quiz Question
 *
 * Core entity for the current theory-question bank.
 * The active delivery workflow now relies on isActive + status synchronization
 * from admin CRUD and import flows instead of a separate manual publish mode.
 */
@Entity
@Table(name = "quiz_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QuizQuestion extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionAr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionEn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionNl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionFr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DifficultyLevel difficultyLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_sign_id")
    private RoadSign roadSign;

    // Law #6 (Grand Contract): Generic content image URL
    // Move ImageUrl from TrafficSign to QuizQuestion to make it generic
    @Column(name = "content_image_url", columnDefinition = "TEXT")
    private String contentImageUrl;

    @Column(columnDefinition = "TEXT")
    private String explanationAr;

    @Column(columnDefinition = "TEXT")
    private String explanationEn;

    @Column(columnDefinition = "TEXT")
    private String explanationNl;

    @Column(columnDefinition = "TEXT")
    private String explanationFr;

    // Legacy error-explanation fields kept only for backward-compatible reads.
    // The current admin workflow no longer writes or exposes them.
    @Column(name = "error_explanation_ar", columnDefinition = "TEXT")
    private String errorExplanationAr;

    @Column(name = "error_explanation_en", columnDefinition = "TEXT")
    private String errorExplanationEn;

    @Column(name = "error_explanation_nl", columnDefinition = "TEXT")
    private String errorExplanationNl;

    @Column(name = "error_explanation_fr", columnDefinition = "TEXT")
    private String errorExplanationFr;

    // Legacy metadata retained for compatibility with older rows/imports.
    @Enumerated(EnumType.STRING)
    @Column(name = "typical_error_type", length = 30)
    private TypicalErrorType typicalErrorType;

    // Legacy flag retained for compatibility; current admin workflow forces false.
    @Column(name = "context_specific")
    private Boolean contextSpecific = false;

    // Legacy flag retained for compatibility; current admin workflow forces false.
    @Column(name = "requires_sign_image")
    private Boolean requiresSignImage = false;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Story D4: Question status for publish workflow
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private QuestionStatus status = QuestionStatus.DRAFT;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @BelgianOptionsCount // Story D1: Belgian compliance - 2-3 options
    private List<QuizAnswerOption> options = new ArrayList<>();

    // Enums
    public enum QuestionStatus {
        DRAFT, // Can be edited freely
        PUBLISHED // Compliance-locked, immutable
    }

    public enum QuestionType {
        MULTIPLE_CHOICE,
        TRUE_FALSE,
        IMAGE_BASED
    }

    public enum DifficultyLevel {
        EASY,
        MEDIUM,
        HARD
    }

    public enum TypicalErrorType {
        SIGN_CONFUSION, // Confusion between similar signs
        SUPPLEMENTARY_IGNORED, // Ignoring a supplementary panel
        PRIORITY_MISUNDERSTANDING, // Misunderstanding of priority
        SPEED_LIMIT_ERROR, // Error in speed limits
        ZONE_CONFUSION, // Confusion between zones
        RULE_OVERGENERALIZATION, // Overgeneralizing a rule in the wrong context
        OTHER
    }

    // Helper methods
    public void addOption(QuizAnswerOption option) {
        options.add(option);
        option.setQuestion(this);
    }

    public void removeOption(QuizAnswerOption option) {
        options.remove(option);
        option.setQuestion(null);
    }

    @Transient
    public int getExpectedOptionCount() {
        return difficultyLevel == DifficultyLevel.HARD ? 2 : 3;
    }

    @Transient
    public List<QuizAnswerOption> getDeliverableOptions() {
        if (options == null || options.isEmpty()) {
            return List.of();
        }
        return options.stream()
                .sorted(Comparator.comparing(
                        QuizAnswerOption::getDisplayOrder,
                        Comparator.nullsLast(Integer::compareTo)))
                .limit(getExpectedOptionCount())
                .toList();
    }

    @Override
    protected void normalizeTextFields() {
        questionAr = TextNormalizer.normalize(questionAr);
        questionEn = TextNormalizer.normalize(questionEn);
        questionNl = TextNormalizer.normalize(questionNl);
        questionFr = TextNormalizer.normalize(questionFr);
        explanationAr = TextNormalizer.normalize(explanationAr);
        explanationEn = TextNormalizer.normalize(explanationEn);
        explanationNl = TextNormalizer.normalize(explanationNl);
        explanationFr = TextNormalizer.normalize(explanationFr);
        errorExplanationAr = TextNormalizer.normalize(errorExplanationAr);
        errorExplanationEn = TextNormalizer.normalize(errorExplanationEn);
        errorExplanationNl = TextNormalizer.normalize(errorExplanationNl);
        errorExplanationFr = TextNormalizer.normalize(errorExplanationFr);
    }
}
