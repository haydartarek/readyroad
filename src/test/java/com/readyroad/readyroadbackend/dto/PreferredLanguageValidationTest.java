package com.readyroad.readyroadbackend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PreferredLanguageValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void acceptsAllSupportedLanguages() {
        for (String language : new String[] {"en", "nl", "fr", "ar"}) {
            assertThat(validator.validate(new UpdatePreferredLanguageRequest(language)))
                    .as("language %s", language)
                    .isEmpty();
        }
    }

    @Test
    void rejectsUnsupportedAndMissingLanguages() {
        assertThat(validator.validate(new UpdatePreferredLanguageRequest("de"))).isNotEmpty();
        assertThat(validator.validate(new UpdatePreferredLanguageRequest(null))).isNotEmpty();
    }

    @Test
    void keepsRegistrationLanguageOptionalForLegacyClients() {
        RegisterRequest request = validRegistration();
        assertThat(validator.validate(request)).isEmpty();

        request.setPreferredLanguage("it");
        assertThat(validator.validate(request)).isNotEmpty();
    }

    private RegisterRequest validRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("learner");
        request.setEmail("learner@example.com");
        request.setFullName("Learner User");
        request.setPassword("Secret123!");
        return request;
    }
}
