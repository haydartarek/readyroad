package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.validation.BelgianOptionsCount;
import com.readyroad.readyroadbackend.validation.PublishValidation;
import com.readyroad.readyroadbackend.validation.RequiresTrafficSign;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz Question Entity
 * سؤال الاختبار
 *
 * **Phase 2 Restoration:** Re-enabled January 18, 2026
 * Used for smart quiz generation with 24-hour cooldown
 *
 * **Stories D3/D4:** Belgian Compliance Enforcement
 * - Traffic sign required for exam questions
 * - Publish-time validation gates
 * - Immutable compliance after publish
 */
@Entity
@Table(name = "quiz_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@RequiresTrafficSign(groups = PublishValidation.class) // D3: Traffic sign required for publish
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
    @JoinColumn(name = "traffic_sign_id")
    private TrafficSign trafficSign;

    // ✅ Law #6 (Grand Contract): Generic content image URL
    // نقل ImageUrl من TrafficSign إلى QuizQuestion لجعله عام
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

    // شرح الخطأ المخصص - Error explanations
    @Column(name = "error_explanation_ar", columnDefinition = "TEXT")
    private String errorExplanationAr;

    @Column(name = "error_explanation_en", columnDefinition = "TEXT")
    private String errorExplanationEn;

    @Column(name = "error_explanation_nl", columnDefinition = "TEXT")
    private String errorExplanationNl;

    @Column(name = "error_explanation_fr", columnDefinition = "TEXT")
    private String errorExplanationFr;

    // نوع الخطأ الشائع - Typical error type for this question
    @Enumerated(EnumType.STRING)
    @Column(name = "typical_error_type", length = 30)
    private TypicalErrorType typicalErrorType;

    // هل الشرح مخصص للعلامة والسياق - Context-specific explanation
    @Column(name = "context_specific")
    private Boolean contextSpecific = true;

    // هل يتطلب صورة العلامة - Requires sign image
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
        SIGN_CONFUSION, // خلط بين علامات متشابهة
        SUPPLEMENTARY_IGNORED, // تجاهل لوحة تكميلية
        PRIORITY_MISUNDERSTANDING, // فهم خاطئ للأولوية
        SPEED_LIMIT_ERROR, // خطأ في حدود السرعة
        ZONE_CONFUSION, // خلط بين المناطق
        RULE_OVERGENERALIZATION, // تعميم قاعدة في مكان خاطئ
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
}
