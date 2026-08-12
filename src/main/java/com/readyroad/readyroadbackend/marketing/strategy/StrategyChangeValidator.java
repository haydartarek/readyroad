package com.readyroad.readyroadbackend.marketing.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import org.springframework.stereotype.Component;

@Component
public class StrategyChangeValidator {

    public void validate(StrategyResourceType type, String resourceId, JsonNode data) {
        if (type == null || data == null || !data.isObject()) {
            throw invalid("Strategy resource type and object data are required");
        }
        if (type == StrategyResourceType.ICP) {
            requireText(resourceId, "resourceId");
        } else if (resourceId != null && !resourceId.isBlank()) {
            parseId(resourceId);
        }
        optionalBoolean(data, "active");

        switch (type) {
            case USP -> {
                requireText(data, "title");
                requireText(data, "description");
                requireText(data, "evidenceType");
                requireText(data, "evidenceReference");
                requirePriority(data);
            }
            case ICP -> requireText(data, "name");
            case POSITIONING -> {
                requireText(data, "statement");
                requireArray(data, "brandIdentity");
                requireArray(data, "brandVoice");
            }
            case CONTENT_PILLAR -> {
                requireText(data, "pillarKey");
                requireText(data, "name");
                requirePriority(data);
            }
            case FUNNEL_STAGE -> {
                requireText(data, "stageKey");
                requirePositiveInteger(data, "sequenceNumber");
            }
            case CONVERSION_GOAL -> {
                requireText(data, "goalKey");
                requireText(data, "name");
                requireText(data, "primaryCta");
                requirePositiveLong(data, "funnelStageId");
            }
            case SOCIAL_PROOF -> {
                requireText(data, "proofType");
                requireText(data, "claim");
                requireText(data, "evidenceReference");
            }
        }
    }

    static Long parseId(String resourceId) {
        try {
            long value = Long.parseLong(resourceId);
            if (value <= 0) {
                throw invalid("resourceId must be a positive number");
            }
            return value;
        } catch (NumberFormatException error) {
            throw invalid("resourceId must be a positive number");
        }
    }

    static MarketingTaskExecutionException invalid(String message) {
        return new MarketingTaskExecutionException("VALIDATION_ERROR", message);
    }

    private static void requireText(JsonNode data, String field) {
        JsonNode value = data.get(field);
        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            throw invalid(field + " is required");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw invalid(field + " is required");
        }
    }

    private static void requireArray(JsonNode data, String field) {
        JsonNode value = data.get(field);
        if (value == null || !value.isArray() || value.isEmpty()) {
            throw invalid(field + " must be a non-empty array");
        }
    }

    private static void requirePriority(JsonNode data) {
        JsonNode value = data.get("priority");
        if (value == null || !value.canConvertToInt() || value.asInt() < 0 || value.asInt() > 3) {
            throw invalid("priority must be between 0 and 3");
        }
    }

    private static void requirePositiveInteger(JsonNode data, String field) {
        JsonNode value = data.get(field);
        if (value == null || !value.canConvertToInt() || value.asInt() <= 0) {
            throw invalid(field + " must be a positive integer");
        }
    }

    private static void requirePositiveLong(JsonNode data, String field) {
        JsonNode value = data.get(field);
        if (value == null || !value.canConvertToLong() || value.asLong() <= 0) {
            throw invalid(field + " must be a positive number");
        }
    }

    private static void optionalBoolean(JsonNode data, String field) {
        JsonNode value = data.get(field);
        if (value != null && !value.isBoolean()) {
            throw invalid(field + " must be boolean");
        }
    }
}
