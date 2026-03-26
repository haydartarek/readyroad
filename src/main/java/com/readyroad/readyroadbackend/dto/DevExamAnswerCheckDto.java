package com.readyroad.readyroadbackend.dto;

public class DevExamAnswerCheckDto {

    private boolean correct;
    private Long correctChoiceId;

    public DevExamAnswerCheckDto() {
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Long getCorrectChoiceId() {
        return correctChoiceId;
    }

    public void setCorrectChoiceId(Long correctChoiceId) {
        this.correctChoiceId = correctChoiceId;
    }
}
