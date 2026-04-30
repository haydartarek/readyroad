package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistent mixed-sign random practice exam session for /practice/random.
 *
 * Questions are assigned once when the session starts and stay locked for this
 * user for 24 hours, even if the session is later abandoned or expires.
 */
@Entity
@Table(name = "sign_random_practice_sessions", indexes = {
        @Index(name = "idx_srps_user_status", columnList = "user_id, status"),
        @Index(name = "idx_srps_user_started", columnList = "user_id, started_at"),
        @Index(name = "idx_srps_completed_at", columnList = "completed_at")
})
public class SignRandomPracticeSession {

    public enum SessionStatus {
        IN_PROGRESS,
        COMPLETED,
        EXPIRED,
        ABANDONED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 50;

    @Column(name = "answered_count", nullable = false)
    private Integer answeredCount = 0;

    @Column(name = "correct_count", nullable = false)
    private Integer correctCount = 0;

    @Column(name = "passing_score", nullable = false)
    private Integer passingScore = 41;

    @Column(name = "score_pct")
    private Double scorePct;

    @Column(name = "passed")
    private Boolean passed;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status = SessionStatus.IN_PROGRESS;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    private List<SignRandomPracticeQuestion> questions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        startedAt = startedAt == null ? now : startedAt;
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getAnsweredCount() {
        return answeredCount;
    }

    public void setAnsweredCount(Integer answeredCount) {
        this.answeredCount = answeredCount;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public Double getScorePct() {
        return scorePct;
    }

    public void setScorePct(Double scorePct) {
        this.scorePct = scorePct;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<SignRandomPracticeQuestion> getQuestions() {
        return questions;
    }
}
