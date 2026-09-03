package com.readyroad.readyroadbackend.marketing.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.errors.OpenAIServiceException;
import java.io.IOException;
import java.io.InputStream;

/** Maps provider failures without persisting prompts, credentials or raw error messages. */
public final class OpenAIRequestFailure {
    private OpenAIRequestFailure() {}

    public static OpenAIContentGenerationException from(OpenAIServiceException error) {
        return classify(error.statusCode(), error.code().orElse(""), error.type().orElse(""));
    }

    public static OpenAIContentGenerationException from(int status, InputStream body, ObjectMapper mapper) {
        if (status == 429 && body != null) {
            try {
                JsonNode parsed = mapper.readTree(body.readNBytes(16_384));
                if (parsed != null) {
                    JsonNode error = parsed.path("error");
                    return classify(status, error.path("code").asText(""), error.path("type").asText(""));
                }
            } catch (IOException ignored) {
                // An unreadable error body must not replace the original HTTP status.
            }
        }
        return classify(status, "", "");
    }

    private static OpenAIContentGenerationException classify(int status, String code, String type) {
        if (status == 429 && ("insufficient_quota".equals(code) || "insufficient_quota".equals(type))) {
            return new OpenAIContentGenerationException("OPENAI_QUOTA_EXHAUSTED",
                    "OpenAI API quota is exhausted. Check project billing and usage limits before retrying.");
        }
        String mapped = switch (status) {
            case 429 -> "HTTP_429";
            case 502 -> "HTTP_502";
            case 503 -> "HTTP_503";
            case 504 -> "HTTP_504";
            case 401, 403 -> "INVALID_API_KEY";
            case 400 -> "OPENAI_VALIDATION_FAILURE";
            default -> "OPENAI_HTTP_" + status;
        };
        return new OpenAIContentGenerationException(mapped, "OpenAI request failed with HTTP " + status);
    }
}
