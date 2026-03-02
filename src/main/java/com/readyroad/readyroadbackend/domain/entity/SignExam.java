package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * One of the two exam templates associated with a {@link RoadSign}.
 * Each exam contains 15 questions: 6 EASY + 6 MEDIUM + 3 HARD.
 * Passing score: 12 / 15.
 */
@Entity
@Table(name = "sign_exams")
public class SignExam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sign_id", nullable = false)
    private RoadSign sign;

    /** 1 or 2 */
    @Column(name = "exam_number", nullable = false)
    private Integer examNumber;

    @Column(name = "passing_score", nullable = false)
    private Integer passingScore = 12;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 15;

    @Column(name = "easy_count", nullable = false)
    private Integer easyCount = 6;

    @Column(name = "medium_count", nullable = false)
    private Integer mediumCount = 6;

    @Column(name = "hard_count", nullable = false)
    private Integer hardCount = 3;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    private List<SignExamQuestion> examQuestions = new ArrayList<>();

    // ── Helpers ───────────────────────────────────────────────────────────────
    public void addExamQuestion(SignExamQuestion eq) {
        eq.setExam(this);
        examQuestions.add(eq);
    }

    public void clearExamQuestions() {
        examQuestions.forEach(eq -> eq.setExam(null));
        examQuestions.clear();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public RoadSign getSign()                       { return sign; }
    public void     setSign(RoadSign v)             { this.sign = v; }

    public Integer getExamNumber()                  { return examNumber; }
    public void    setExamNumber(Integer v)         { this.examNumber = v; }

    public Integer getPassingScore()                { return passingScore; }
    public void    setPassingScore(Integer v)       { this.passingScore = v; }

    public Integer getTotalQuestions()              { return totalQuestions; }
    public void    setTotalQuestions(Integer v)     { this.totalQuestions = v; }

    public Integer getEasyCount()                   { return easyCount; }
    public void    setEasyCount(Integer v)          { this.easyCount = v; }

    public Integer getMediumCount()                 { return mediumCount; }
    public void    setMediumCount(Integer v)        { this.mediumCount = v; }

    public Integer getHardCount()                   { return hardCount; }
    public void    setHardCount(Integer v)          { this.hardCount = v; }

    public Boolean getIsActive()                    { return isActive; }
    public void    setIsActive(Boolean v)           { this.isActive = v; }

    public List<SignExamQuestion> getExamQuestions() { return examQuestions; }
}
