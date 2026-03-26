package com.readyroad.readyroadbackend.dto;

import java.util.List;

public class DevExamQuestionDto {

    private Long id;
    private String difficulty;
    private String question; // resolved to requested language
    private String explanation; // resolved to requested language (may be null)
    private List<DevExamChoiceDto> choices;

    // ─── Constructors ────────────────────────────────────────────────────────

    public DevExamQuestionDto() {
    }

    // ─── Getters / Setters ──────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<DevExamChoiceDto> getChoices() {
        return choices;
    }

    public void setChoices(List<DevExamChoiceDto> choices) {
        this.choices = choices;
    }
}
