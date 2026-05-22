package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Exam Simulation Entity - Phase 5
 * Represents a 50-question exam simulation for Belgian driving license
 * preparation.
 *
 * Compliance:
 * - 50 questions per exam
 * - 30 minutes time limit
 * - 41/50 passing score (82%)
 */
@Entity
@Table(name = "exam_simulations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExamSimulation extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 50;

    @Column(name = "correct_answers")
    private Integer correctAnswers;

    @Column(name = "score_percentage", columnDefinition = "DECIMAL(5,2)")
    private Double scorePercentage;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExamStatus status;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamSimulationQuestion> questions = new ArrayList<>();

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamSimulationAnswer> answers = new ArrayList<>();

    public enum ExamStatus {
        IN_PROGRESS,
        COMPLETED,
        ABANDONED,
        EXPIRED
    }

    // Helper methods
    public boolean isPassed() {
        return correctAnswers != null && correctAnswers >= 41;
    }

    /**
     * Check if exam has expired (UTC-aware)
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Get remaining time in minutes (UTC-aware)
     */
    public long getRemainingMinutes() {
        if (isExpired() || isCompleted()) {
            return 0;
        }
        Duration remaining = Duration.between(Instant.now(), expiresAt);
        return Math.max(0, remaining.toMinutes());
    }

    /**
     * Get remaining time in seconds (UTC-aware)
     */
    public long getRemainingSeconds() {
        if (isExpired() || isCompleted()) {
            return 0;
        }
        Duration remaining = Duration.between(Instant.now(), expiresAt);
        return Math.max(0, remaining.getSeconds());
    }

    public boolean isCompleted() {
        return status == ExamStatus.COMPLETED;
    }
}
