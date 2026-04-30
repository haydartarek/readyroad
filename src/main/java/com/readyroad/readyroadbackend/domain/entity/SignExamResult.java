package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Records every sign-exam submission for a user.
 */
@Entity
@Table(name = "sign_exam_results", indexes = {
        @Index(name = "idx_ser_user_sign", columnList = "user_id, sign_id"),
        @Index(name = "idx_ser_passed", columnList = "passed"),
        @Index(name = "idx_ser_completed_at", columnList = "completed_at")
})
public class SignExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FK → users.id (cascade delete handled by DB constraint) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** FK → road_signs.id */
    @Column(name = "sign_id", nullable = false)
    private Long signId;

    /** Short code such as "A1a" — stored for quick look-ups without a JOIN */
    @Column(name = "sign_code", nullable = false, length = 50)
    private String signCode;

    @Column(name = "exam_number", nullable = false)
    private Integer examNumber = 1;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "answered_count", nullable = false)
    private Integer answeredCount;

    @Column(name = "correct_count", nullable = false)
    private Integer correctCount;

    @Column(name = "required_to_pass", nullable = false)
    private Integer requiredToPass;

    /** 0.00 – 100.00 */
    @Column(name = "score_pct", nullable = false)
    private Double scorePct;

    @Column(name = "passed", nullable = false)
    private Boolean passed = false;

    @Lob
    @Column(name = "question_results_json", columnDefinition = "LONGTEXT")
    private String questionResultsJson;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (completedAt == null)
            completedAt = now;
        createdAt = now;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long v) {
        this.userId = v;
    }

    public Long getSignId() {
        return signId;
    }

    public void setSignId(Long v) {
        this.signId = v;
    }

    public String getSignCode() {
        return signCode;
    }

    public void setSignCode(String v) {
        this.signCode = v;
    }

    public Integer getExamNumber() {
        return examNumber;
    }

    public void setExamNumber(Integer v) {
        this.examNumber = v;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer v) {
        this.totalQuestions = v;
    }

    public Integer getAnsweredCount() {
        return answeredCount;
    }

    public void setAnsweredCount(Integer v) {
        this.answeredCount = v;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer v) {
        this.correctCount = v;
    }

    public Integer getRequiredToPass() {
        return requiredToPass;
    }

    public void setRequiredToPass(Integer v) {
        this.requiredToPass = v;
    }

    public Double getScorePct() {
        return scorePct;
    }

    public void setScorePct(Double v) {
        this.scorePct = v;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean v) {
        this.passed = v;
    }

    public String getQuestionResultsJson() {
        return questionResultsJson;
    }

    public void setQuestionResultsJson(String v) {
        this.questionResultsJson = v;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime v) {
        this.completedAt = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
