package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Quiz User Answer Entity
 *
 * **Phase 2 Restoration:** Re-enabled January 18, 2026
 * **Fix Applied:** February 6, 2026 - Updated to match database schema
 * Tracks individual user answers within a quiz attempt
 * 
 * Database Schema: Uses polymorphic reference (question_type + question_ref_id)
 * instead of direct foreign key to support both PRACTICE and EXAM questions
 */
@Entity
@Table(name = "quiz_user_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizUserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, columnDefinition = "ENUM('PRACTICE','EXAM')")
    private QuestionType questionType;

    @Column(name = "question_ref_id", nullable = false)
    private Long questionRefId;

    @Column(name = "selected_option", nullable = false)
    private Integer selectedOption;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "answered_at", nullable = false, updatable = false)
    private LocalDateTime answeredAt;

    @Column(name = "is_test_data", nullable = false)
    private Boolean isTestData = false;

    @PrePersist
    protected void onAnswer() {
        if (answeredAt == null) {
            answeredAt = LocalDateTime.now();
        }
    }

    public enum QuestionType {
        PRACTICE,
        EXAM
    }
}