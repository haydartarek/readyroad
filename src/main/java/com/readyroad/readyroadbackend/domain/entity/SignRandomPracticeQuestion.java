package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Assigned question row inside a mixed random sign exam session.
 */
@Entity
@Table(name = "sign_random_practice_questions", indexes = {
        @Index(name = "idx_srpq_session", columnList = "session_id"),
        @Index(name = "idx_srpq_question", columnList = "question_id"),
        @Index(name = "idx_srpq_answered", columnList = "answered_at")
})
public class SignRandomPracticeQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SignRandomPracticeSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private SignQuestion question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_choice_id")
    private SignChoice selectedChoice;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "was_timeout", nullable = false)
    private Boolean wasTimeout = false;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public SignRandomPracticeSession getSession() {
        return session;
    }

    public void setSession(SignRandomPracticeSession session) {
        this.session = session;
    }

    public SignQuestion getQuestion() {
        return question;
    }

    public void setQuestion(SignQuestion question) {
        this.question = question;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public SignChoice getSelectedChoice() {
        return selectedChoice;
    }

    public void setSelectedChoice(SignChoice selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public Boolean getWasTimeout() {
        return wasTimeout;
    }

    public void setWasTimeout(Boolean wasTimeout) {
        this.wasTimeout = wasTimeout;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
