package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dev_exam_questions")
public class DevExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private DevExamCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DevExamDifficulty difficulty;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionEn;

    @Column(columnDefinition = "TEXT")
    private String questionAr;

    @Column(columnDefinition = "TEXT")
    private String questionNl;

    @Column(columnDefinition = "TEXT")
    private String questionFr;

    @Column(columnDefinition = "TEXT")
    private String explanationEn;

    @Column(columnDefinition = "TEXT")
    private String explanationAr;

    @Column(columnDefinition = "TEXT")
    private String explanationNl;

    @Column(columnDefinition = "TEXT")
    private String explanationFr;

    @Column(nullable = false)
    private Boolean isActive = true;

    // ─── Getters / Setters ──────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DevExamCategory getCategory() {
        return category;
    }

    public void setCategory(DevExamCategory category) {
        this.category = category;
    }

    public DevExamDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DevExamDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestionEn() {
        return questionEn;
    }

    public void setQuestionEn(String questionEn) {
        this.questionEn = questionEn;
    }

    public String getQuestionAr() {
        return questionAr;
    }

    public void setQuestionAr(String questionAr) {
        this.questionAr = questionAr;
    }

    public String getQuestionNl() {
        return questionNl;
    }

    public void setQuestionNl(String questionNl) {
        this.questionNl = questionNl;
    }

    public String getQuestionFr() {
        return questionFr;
    }

    public void setQuestionFr(String questionFr) {
        this.questionFr = questionFr;
    }

    public String getExplanationEn() {
        return explanationEn;
    }

    public void setExplanationEn(String explanationEn) {
        this.explanationEn = explanationEn;
    }

    public String getExplanationAr() {
        return explanationAr;
    }

    public void setExplanationAr(String explanationAr) {
        this.explanationAr = explanationAr;
    }

    public String getExplanationNl() {
        return explanationNl;
    }

    public void setExplanationNl(String explanationNl) {
        this.explanationNl = explanationNl;
    }

    public String getExplanationFr() {
        return explanationFr;
    }

    public void setExplanationFr(String explanationFr) {
        this.explanationFr = explanationFr;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
