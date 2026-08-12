package com.readyroad.readyroadbackend.marketing.strategy;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StrategyChangeValidatorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private StrategyChangeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StrategyChangeValidator();
    }

    @Test
    void acceptsACompleteEvidenceBackedUsp() {
        var data = objectMapper.createObjectNode()
                .put("title", "Four-language learning")
                .put("description", "Verified ReadyRoad capability")
                .put("evidenceType", "READYROAD_FEATURE")
                .put("evidenceReference", "SUPPORTED_LANGUAGES")
                .put("active", true)
                .put("priority", 2);

        assertThatCode(() -> validator.validate(StrategyResourceType.USP, null, data))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsUspWithoutEvidence() {
        var data = objectMapper.createObjectNode()
                .put("title", "Unsupported claim")
                .put("description", "No evidence supplied")
                .put("priority", 1);

        assertValidationError(() -> validator.validate(StrategyResourceType.USP, null, data), "evidenceType");
    }

    @Test
    void rejectsSocialProofWithoutEvidenceReference() {
        var data = objectMapper.createObjectNode()
                .put("proofType", "REVIEW")
                .put("claim", "Unverified review");

        assertValidationError(
                () -> validator.validate(StrategyResourceType.SOCIAL_PROOF, null, data),
                "evidenceReference");
    }

    @Test
    void rejectsConversionGoalWithoutAnApprovedFunnelReference() {
        var data = objectMapper.createObjectNode()
                .put("goalKey", "START_EXAM")
                .put("name", "Start exam")
                .put("primaryCta", "Start exam")
                .put("funnelStageId", 0);

        assertValidationError(
                () -> validator.validate(StrategyResourceType.CONVERSION_GOAL, null, data),
                "funnelStageId");
    }

    private static void assertValidationError(Runnable action, String message) {
        assertThatThrownBy(action::run)
                .isInstanceOf(MarketingTaskExecutionException.class)
                .hasMessageContaining(message)
                .satisfies(error -> org.assertj.core.api.Assertions.assertThat(
                                ((MarketingTaskExecutionException) error).errorCode())
                        .isEqualTo("VALIDATION_ERROR"));
    }
}
