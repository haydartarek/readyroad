package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Join entity: maps a {@link SignQuestion} to a {@link SignExam} with an ordering.
 * No question may appear in both exams of the same sign.
 */
@Entity
@Table(name = "sign_exam_questions")
public class SignExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private SignExam exam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private SignQuestion question;

    /** Position within the exam (1–15). */
    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                           { return id; }

    public SignExam getExam()                     { return exam; }
    public void     setExam(SignExam v)           { this.exam = v; }

    public SignQuestion getQuestion()             { return question; }
    public void         setQuestion(SignQuestion v) { this.question = v; }

    public Integer getQuestionOrder()             { return questionOrder; }
    public void    setQuestionOrder(Integer v)    { this.questionOrder = v; }

    public LocalDateTime getCreatedAt()           { return createdAt; }
}
