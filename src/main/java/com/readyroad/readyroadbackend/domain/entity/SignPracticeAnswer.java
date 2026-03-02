package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * One answered question within a {@link SignPracticeSession}.
 * Each (session, question) pair is unique — the UNIQUE KEY in the
 * DB prevents answering the same question twice in the same session.
 */
@Entity
@Table(name = "sign_practice_answers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_spa_session_question",
                columnNames = {"session_id", "question_id"}))
public class SignPracticeAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SignPracticeSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private SignQuestion question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "choice_id", nullable = false)
    private SignChoice choice;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "answered_at", nullable = false, updatable = false)
    private LocalDateTime answeredAt;

    // ── Lifecycle ────────────────────────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        answeredAt = LocalDateTime.now();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                           { return id; }

    public SignPracticeSession getSession()       { return session; }
    public void setSession(SignPracticeSession v) { this.session = v; }

    public SignQuestion getQuestion()             { return question; }
    public void setQuestion(SignQuestion v)       { this.question = v; }

    public SignChoice getChoice()                 { return choice; }
    public void setChoice(SignChoice v)           { this.choice = v; }

    public Boolean getIsCorrect()                 { return isCorrect; }
    public void    setIsCorrect(Boolean v)        { this.isCorrect = v; }

    public Integer getTimeTakenSeconds()          { return timeTakenSeconds; }
    public void    setTimeTakenSeconds(Integer v) { this.timeTakenSeconds = v; }

    public LocalDateTime getAnsweredAt()          { return answeredAt; }
}
