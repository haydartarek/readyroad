package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_weak_areas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_category", columnNames = { "user_id", "category" }),
        @UniqueConstraint(name = "uk_user_sign", columnNames = { "user_id", "traffic_sign_code" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWeakArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "traffic_sign_code", length = 50)
    private String trafficSignCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_sign_id")
    private RoadSign roadSign;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 0;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers = 0;

    @Column(name = "wrong_answers", nullable = false)
    private Integer wrongAnswers = 0;

    @Column(name = "accuracy_percentage", nullable = false)
    private Double accuracyPercentage = 0.0;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    public void updateStats(boolean isCorrect) {
        totalQuestions++;
        if (isCorrect) {
            correctAnswers++;
        } else {
            wrongAnswers++;
        }
        this.accuracyPercentage = (double) correctAnswers / totalQuestions * 100.0;
        this.lastUpdated = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        if (lastUpdated == null) {
            lastUpdated = LocalDateTime.now();
        }
    }
}
