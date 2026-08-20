package com.readyroad.readyroadbackend.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Exam Simulation Question - Phase 5
 * Maps questions to exam simulations in a specific order.
 */
@Entity
@Table(name = "exam_simulation_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExamSimulationQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private ExamSimulation exam;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "presented_at")
    private LocalDateTime presentedAt;

    @Column(name = "historical_snapshot_version")
    @JsonIgnore
    private Short historicalSnapshotVersion;

    @Column(name = "historical_snapshot_json", columnDefinition = "TEXT")
    @JsonIgnore
    private String historicalSnapshotJson;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private QuizQuestion question;
}
