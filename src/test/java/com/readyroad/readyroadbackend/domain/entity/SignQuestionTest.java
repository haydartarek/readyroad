package com.readyroad.readyroadbackend.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;

class SignQuestionTest {

    @Test
    void mediumAllowedQuestionDeliversOnlyTwoChoices() {
        SignQuestion question = question(SignDifficulty.MEDIUM, SignQuestionType.IS_IT_ALLOWED, 3);

        assertThat(question.getExpectedChoiceCount()).isEqualTo(2);
        assertThat(question.getDeliverableChoices()).hasSize(2);
    }

    @Test
    void regularMediumQuestionStillDeliversThreeChoices() {
        SignQuestion question = question(SignDifficulty.MEDIUM, SignQuestionType.WHAT_DOES_IT_MEAN, 3);

        assertThat(question.getExpectedChoiceCount()).isEqualTo(3);
        assertThat(question.getDeliverableChoices()).hasSize(3);
    }

    private SignQuestion question(SignDifficulty difficulty, SignQuestionType type, int choiceCount) {
        SignQuestion question = new SignQuestion();
        question.setDifficulty(difficulty);
        question.setQuestionType(type);

        for (int order = 1; order <= choiceCount; order++) {
            SignChoice choice = new SignChoice();
            choice.setDisplayOrder(order);
            question.addChoice(choice);
        }
        return question;
    }
}
