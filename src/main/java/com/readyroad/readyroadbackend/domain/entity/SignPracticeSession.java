package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A stateful practice session for a specific road sign.
 *
 * <p>One session = all active questions for one sign, shuffled.
 * The session moves from IN_PROGRESS → COMPLETED once every
 * question has been answered.</p>
 *
 * <p>Intentionally does NOT extend {@link BaseEntity}: the relevant
 * timestamps here are {@code startedAt} and {@code completedAt},
 * not the generic {@code createdAt}/{@code updatedAt} pattern.</p>
 */
@Entity
@Table(name = "sign_practice_sessions",
        indexes = {
                @Index(name = "idx_sps_user_sign",   columnList = "user_id, sign_id"),
                @Index(name = "idx_sps_user_status", columnList = "user_id, status")
        })
public class SignPracticeSession {

    // ── Status ───────────────────────────────────────────────────────────────
    public enum SessionStatus { IN_PROGRESS, COMPLETED, ABANDONED }

    // ── Fields ───────────────────────────────────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sign_id", nullable = false)
    private RoadSign sign;

    /** Denormalised for quick lookup without joining road_signs. */
    @Column(name = "sign_code", nullable = false, length = 50)
    private String signCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status = SessionStatus.IN_PROGRESS;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 0;

    @Column(name = "correct_count", nullable = false)
    private Integer correctCount = 0;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // ── Relationship ─────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("answeredAt ASC")
    private List<SignPracticeAnswer> answers = new ArrayList<>();

    // ── Lifecycle ────────────────────────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    public void addAnswer(SignPracticeAnswer answer) {
        answer.setSession(this);
        answers.add(answer);
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long            getId()             { return id; }

    public User            getUser()           { return user; }
    public void            setUser(User v)     { this.user = v; }

    public RoadSign        getSign()           { return sign; }
    public void            setSign(RoadSign v) { this.sign = v; }

    public String          getSignCode()           { return signCode; }
    public void            setSignCode(String v)   { this.signCode = v; }

    public SessionStatus   getStatus()             { return status; }
    public void            setStatus(SessionStatus v) { this.status = v; }

    public Integer         getTotalQuestions()     { return totalQuestions; }
    public void            setTotalQuestions(Integer v) { this.totalQuestions = v; }

    public Integer         getCorrectCount()       { return correctCount; }
    public void            setCorrectCount(Integer v)   { this.correctCount = v; }

    public LocalDateTime   getStartedAt()          { return startedAt; }

    public LocalDateTime   getCompletedAt()        { return completedAt; }
    public void            setCompletedAt(LocalDateTime v) { this.completedAt = v; }

    public List<SignPracticeAnswer> getAnswers()   { return answers; }
}
