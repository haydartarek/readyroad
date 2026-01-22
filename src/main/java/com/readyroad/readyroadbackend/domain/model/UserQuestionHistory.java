package com.readyroad.readyroadbackend.domain.model;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Tracks when users see questions to enforce 24h cooldown (Law #1).
 * Enhanced in Phase 4 to track performance for adaptive difficulty (Law #2).
 * Generic design - works for any content domain.
 */
@Entity
@Table(name = "user_question_history", indexes = {
    @Index(name = "idx_user_question_history_user_answered", columnList = "user_id,answered_at"),
    @Index(name = "idx_user_question_history_question_answered", columnList = "question_id,answered_at"),
    @Index(name = "idx_user_question_history_lookup", columnList = "user_id,question_id,answered_at"),
    @Index(name = "idx_user_question_history_perf", columnList = "user_id,answered_at,is_correct")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQuestionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;

    /**
     * Phase 4: Performance tracking for adaptive difficulty (Law #2)
     * NULL = question shown but not answered yet (cooldown only)
     * TRUE/FALSE = answer correctness (enables adaptive difficulty)
     */
    @Column(name = "is_correct")
    private Boolean isCorrect;

    /**
     * Phase 4: Time taken to answer (seconds)
     * Used for advanced performance analysis
     * NULL = not tracked or not answered
     */
    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    // Optional relationships (LAZY to avoid loading unless needed)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private QuizQuestion question;
}
