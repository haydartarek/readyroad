package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Exam Simulation Answer - Phase 5
 * Records user answers for exam simulation questions.
 * Updated for Story A2: Submit Exam Answer
 * Uses Instant for UTC-aware timestamps
 */
@Entity
@Table(name = "exam_simulation_answers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExamSimulationAnswer extends BaseEntity {

    public enum AnswerState {
        ANSWERED,
        TIMED_OUT
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private ExamSimulation exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuizAnswerOption selectedOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correct_option_id")
    private QuizAnswerOption correctOption;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "time_taken_seconds", nullable = false)
    private Integer timeTakenSeconds;

    @Column(name = "answered_at", nullable = false)
    private Instant answeredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_state", nullable = false, length = 20)
    @Builder.Default
    private AnswerState answerState = AnswerState.ANSWERED;

    @Column(name = "timed_out_at")
    private Instant timedOutAt;

    public boolean isTimedOut() {
        return answerState == AnswerState.TIMED_OUT;
    }
}
