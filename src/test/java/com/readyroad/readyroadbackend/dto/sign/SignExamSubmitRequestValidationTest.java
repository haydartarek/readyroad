package com.readyroad.readyroadbackend.dto.sign;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SignExamSubmitRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("Allows an empty answers list")
    void allowsEmptyAnswersList() {
        SignExamSubmitRequest request = new SignExamSubmitRequest(List.of());

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    @DisplayName("Rejects a missing answers list")
    void rejectsMissingAnswersList() {
        SignExamSubmitRequest request = new SignExamSubmitRequest(null);

        assertThat(validator.validate(request))
                .singleElement()
                .extracting(violation -> violation.getMessage())
                .isEqualTo("answers list is required");
    }
}
