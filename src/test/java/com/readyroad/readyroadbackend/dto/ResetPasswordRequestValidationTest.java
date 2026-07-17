package com.readyroad.readyroadbackend.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResetPasswordRequestValidationTest {

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
    void acceptsPasswordThatMatchesRegistrationPolicy() {
        ResetPasswordRequest request = requestWithPassword("NewSecure1!");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsWeakPasswordEvenWhenItHasEightCharacters() {
        ResetPasswordRequest request = requestWithPassword("password");

        assertThat(validator.validate(request))
                .anySatisfy(violation -> assertThat(violation.getMessage())
                        .isEqualTo("Password must contain uppercase, lowercase, number and special character"));
    }

    private ResetPasswordRequest requestWithPassword(String password) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("reset-token");
        request.setNewPassword(password);
        return request;
    }
}
