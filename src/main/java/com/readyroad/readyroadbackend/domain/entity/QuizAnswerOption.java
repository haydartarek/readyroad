package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Quiz Answer Option Entity
 * خيار إجابة السؤال
 *
 * **Phase 2 Restoration:** Re-enabled January 18, 2026
 * Represents one answer option for a quiz question (typically 2-3 options per
 * question)
 */
@Entity
@Table(name = "quiz_answer_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class QuizAnswerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionTextAr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionTextEn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionTextNl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionTextFr;

    @Column(nullable = false)
    private Boolean isCorrect = false;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
