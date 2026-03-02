package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Audit log for a single execution of the Sign Quiz Importer.
 */
@Entity
@Table(name = "sign_import_runs")
public class SignImportRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy = "SYSTEM";

    @Column(nullable = false, length = 10)
    private String status;           // SUCCESS | PARTIAL | FAILED

    @Column(name = "signs_processed", nullable = false)
    private Integer signsProcessed = 0;

    @Column(name = "signs_created", nullable = false)
    private Integer signsCreated = 0;

    @Column(name = "signs_updated", nullable = false)
    private Integer signsUpdated = 0;

    @Column(name = "signs_skipped", nullable = false)
    private Integer signsSkipped = 0;

    @Column(name = "questions_created", nullable = false)
    private Integer questionsCreated = 0;

    @Column(name = "questions_updated", nullable = false)
    private Integer questionsUpdated = 0;

    @Column(name = "exams_created", nullable = false)
    private Integer examsCreated = 0;

    @Column(name = "errors_count", nullable = false)
    private Integer errorsCount = 0;

    @Column(name = "error_summary", columnDefinition = "TEXT")
    private String errorSummary;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                           { return id; }

    public String getPerformedBy()                { return performedBy; }
    public void   setPerformedBy(String v)        { this.performedBy = v; }

    public String getStatus()                     { return status; }
    public void   setStatus(String v)             { this.status = v; }

    public Integer getSignsProcessed()            { return signsProcessed; }
    public void    setSignsProcessed(Integer v)   { this.signsProcessed = v; }

    public Integer getSignsCreated()              { return signsCreated; }
    public void    setSignsCreated(Integer v)     { this.signsCreated = v; }

    public Integer getSignsUpdated()              { return signsUpdated; }
    public void    setSignsUpdated(Integer v)     { this.signsUpdated = v; }

    public Integer getSignsSkipped()              { return signsSkipped; }
    public void    setSignsSkipped(Integer v)     { this.signsSkipped = v; }

    public Integer getQuestionsCreated()          { return questionsCreated; }
    public void    setQuestionsCreated(Integer v) { this.questionsCreated = v; }

    public Integer getQuestionsUpdated()          { return questionsUpdated; }
    public void    setQuestionsUpdated(Integer v) { this.questionsUpdated = v; }

    public Integer getExamsCreated()              { return examsCreated; }
    public void    setExamsCreated(Integer v)     { this.examsCreated = v; }

    public Integer getErrorsCount()               { return errorsCount; }
    public void    setErrorsCount(Integer v)      { this.errorsCount = v; }

    public String getErrorSummary()               { return errorSummary; }
    public void   setErrorSummary(String v)       { this.errorSummary = v; }

    public Long getDurationMs()                   { return durationMs; }
    public void setDurationMs(Long v)             { this.durationMs = v; }

    public LocalDateTime getCreatedAt()           { return createdAt; }
}
