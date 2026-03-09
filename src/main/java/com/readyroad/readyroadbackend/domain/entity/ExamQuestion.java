package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_questions")
public class ExamQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionAr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionEn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionNl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionFr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String option1Ar;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String option1En;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String option1Nl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String option1Fr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String option2Ar;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String option2En;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String option2Nl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String option2Fr;

    // option3 is optional — questions can have 2 or 3 options (Belgian standard)
    @Column(nullable = true, columnDefinition = "TEXT")
    private String option3Ar;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String option3En;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String option3Nl;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String option3Fr;

    // option4 kept for legacy data only — never set by admin CRUD (max 3 options)
    @Column(nullable = true, columnDefinition = "TEXT")
    private String option4Ar;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String option4En;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String option4Nl;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String option4Fr;

    @Column(nullable = false)
    private Integer correctAnswer; // 1, 2, 3, or 4

    @Column(columnDefinition = "TEXT")
    private String explanationAr;

    @Column(columnDefinition = "TEXT")
    private String explanationEn;

    @Column(columnDefinition = "TEXT")
    private String explanationNl;

    @Column(columnDefinition = "TEXT")
    private String explanationFr;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty = DifficultyLevel.MEDIUM;

    @Column(nullable = false)
    private Boolean isImportant = true; // Is this a major topic question?

    @Column(nullable = false)
    private Boolean isActive = true;

    // Getters and Setters
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getQuestionAr() {
        return questionAr;
    }

    public void setQuestionAr(String questionAr) {
        this.questionAr = questionAr;
    }

    public String getQuestionEn() {
        return questionEn;
    }

    public void setQuestionEn(String questionEn) {
        this.questionEn = questionEn;
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

    public String getOption1Ar() {
        return option1Ar;
    }

    public void setOption1Ar(String option1Ar) {
        this.option1Ar = option1Ar;
    }

    public String getOption1En() {
        return option1En;
    }

    public void setOption1En(String option1En) {
        this.option1En = option1En;
    }

    public String getOption1Nl() {
        return option1Nl;
    }

    public void setOption1Nl(String option1Nl) {
        this.option1Nl = option1Nl;
    }

    public String getOption1Fr() {
        return option1Fr;
    }

    public void setOption1Fr(String option1Fr) {
        this.option1Fr = option1Fr;
    }

    public String getOption2Ar() {
        return option2Ar;
    }

    public void setOption2Ar(String option2Ar) {
        this.option2Ar = option2Ar;
    }

    public String getOption2En() {
        return option2En;
    }

    public void setOption2En(String option2En) {
        this.option2En = option2En;
    }

    public String getOption2Nl() {
        return option2Nl;
    }

    public void setOption2Nl(String option2Nl) {
        this.option2Nl = option2Nl;
    }

    public String getOption2Fr() {
        return option2Fr;
    }

    public void setOption2Fr(String option2Fr) {
        this.option2Fr = option2Fr;
    }

    public String getOption3Ar() {
        return option3Ar;
    }

    public void setOption3Ar(String option3Ar) {
        this.option3Ar = option3Ar;
    }

    public String getOption3En() {
        return option3En;
    }

    public void setOption3En(String option3En) {
        this.option3En = option3En;
    }

    public String getOption3Nl() {
        return option3Nl;
    }

    public void setOption3Nl(String option3Nl) {
        this.option3Nl = option3Nl;
    }

    public String getOption3Fr() {
        return option3Fr;
    }

    public void setOption3Fr(String option3Fr) {
        this.option3Fr = option3Fr;
    }

    public String getOption4Ar() {
        return option4Ar;
    }

    public void setOption4Ar(String option4Ar) {
        this.option4Ar = option4Ar;
    }

    public String getOption4En() {
        return option4En;
    }

    public void setOption4En(String option4En) {
        this.option4En = option4En;
    }

    public String getOption4Nl() {
        return option4Nl;
    }

    public void setOption4Nl(String option4Nl) {
        this.option4Nl = option4Nl;
    }

    public String getOption4Fr() {
        return option4Fr;
    }

    public void setOption4Fr(String option4Fr) {
        this.option4Fr = option4Fr;
    }

    public Integer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanationAr() {
        return explanationAr;
    }

    public void setExplanationAr(String explanationAr) {
        this.explanationAr = explanationAr;
    }

    public String getExplanationEn() {
        return explanationEn;
    }

    public void setExplanationEn(String explanationEn) {
        this.explanationEn = explanationEn;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(Boolean isImportant) {
        this.isImportant = isImportant;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
}
