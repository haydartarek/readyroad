package com.readyroad.readyroadbackend.marketing.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.errors.OpenAIException;
import com.openai.errors.OpenAIIoException;
import com.openai.errors.OpenAIInvalidDataException;
import com.openai.errors.OpenAIServiceException;
import com.openai.models.Reasoning;
import com.openai.models.ReasoningEffort;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.StructuredResponseCreateParams;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContext;
import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenAIResponsesContentGenerator implements ContentGenerationClient {

    private static final String INSTRUCTIONS = """
            You are ReadyRoad's educational content adapter. Use only the verified facts in the request.
            Do not add Belgian traffic rules, exceptions, statistics, social proof, guarantees, official status,
            government affiliation, or facts that are absent from the verified source. Preserve legal meaning.
            Write naturally for the requested language; do not translate from another generated version.
            Avoid generic filler, mechanical AI phrasing, keyword stuffing and emojis.
            Return only the requested structured output. Copy language and sourceReference exactly.
            """;

    private final MarketingProperties properties;
    private final ObjectMapper objectMapper;
    private volatile OpenAIClient client;

    @Autowired
    public OpenAIResponsesContentGenerator(MarketingProperties properties, ObjectMapper objectMapper) {
        this(properties, objectMapper, null);
    }

    OpenAIResponsesContentGenerator(MarketingProperties properties, OpenAIClient client) {
        this(properties, new ObjectMapper(), client);
    }

    OpenAIResponsesContentGenerator(
            MarketingProperties properties,
            ObjectMapper objectMapper,
            OpenAIClient client) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.client = client;
    }

    @Override
    public GeneratedContent generate(GenerationRequest request) {
        ensureConfigured();
        StructuredResponseCreateParams<OpenAIStructuredContent> params = ResponseCreateParams.builder()
                .instructions(INSTRUCTIONS)
                .input(prompt(request))
                .reasoning(Reasoning.builder()
                        .effort(ReasoningEffort.of(properties.getContent().getReasoningEffort()))
                        .build())
                .maxOutputTokens(properties.getContent().getMaxOutputTokens())
                .store(false)
                .text(OpenAIStructuredContent.class)
                .model(properties.getContent().getPrimaryModel())
                .build();
        OpenAIClient activeClient = client();
        try {
            JsonNode response = response(activeClient, params.rawParams());
            String outputText = outputText(response);
            OpenAIStructuredContent output = parse(outputText);
            return new GeneratedContent(
                    output.language,
                    output.sourceReference,
                    output.title,
                    output.summary,
                    output.body,
                    output.cta,
                    response.path("model").asText(properties.getContent().getPrimaryModel()),
                    response.path("usage").path("input_tokens").asLong(0),
                    response.path("usage").path("output_tokens").asLong(0),
                    "SUCCEEDED");
        } catch (OpenAIContentGenerationException expected) {
            throw expected;
        } catch (OpenAIServiceException serviceError) {
            throw serviceFailure(serviceError);
        } catch (OpenAIIoException ioError) {
            invalidate(activeClient);
            throw new OpenAIContentGenerationException(
                    "NETWORK_INTERRUPTION", "OpenAI request failed (" + rootCauseType(ioError) + ")");
        } catch (OpenAIInvalidDataException invalidData) {
            throw new OpenAIContentGenerationException(
                    "MALFORMED_STRUCTURED_OUTPUT", "OpenAI returned an invalid structured response");
        } catch (OpenAIException clientError) {
            throw new OpenAIContentGenerationException("OPENAI_CLIENT_FAILURE", "OpenAI client request failed");
        }
    }

    private JsonNode response(OpenAIClient activeClient, ResponseCreateParams params) {
        try (var raw = activeClient.responses().withRawResponse().create(params)) {
            if (raw.statusCode() < 200 || raw.statusCode() >= 300) {
                throw serviceFailure(raw.statusCode());
            }
            return objectMapper.readTree(raw.body());
        } catch (OpenAIContentGenerationException expected) {
            throw expected;
        } catch (JsonProcessingException invalidBody) {
            throw new OpenAIContentGenerationException(
                    "MALFORMED_STRUCTURED_OUTPUT", "OpenAI returned an unreadable response body");
        } catch (IOException interruptedBody) {
            invalidate(activeClient);
            throw new OpenAIContentGenerationException(
                    "NETWORK_INTERRUPTION", "OpenAI response body failed (" + rootCauseType(interruptedBody) + ")");
        }
    }

    private static String outputText(JsonNode response) {
        for (JsonNode output : response.path("output")) {
            if (!"message".equals(output.path("type").asText())) {
                continue;
            }
            for (JsonNode content : output.path("content")) {
                if ("output_text".equals(content.path("type").asText()) && content.path("text").isTextual()) {
                    return content.path("text").asText();
                }
            }
        }
        throw new OpenAIContentGenerationException(
                "MALFORMED_STRUCTURED_OUTPUT", "OpenAI returned no structured content");
    }

    private OpenAIStructuredContent parse(String outputText) {
        try {
            return objectMapper.readValue(outputText, OpenAIStructuredContent.class);
        } catch (JsonProcessingException invalidJson) {
            throw new OpenAIContentGenerationException(
                    "MALFORMED_STRUCTURED_OUTPUT", "OpenAI returned invalid structured JSON");
        }
    }

    private void invalidate(OpenAIClient failedClient) {
        synchronized (this) {
            if (client == failedClient) {
                client = null;
            }
        }
    }

    private static String rootCauseType(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        String name = current.getClass().getSimpleName();
        return name.isBlank() ? "UnknownIoFailure" : name;
    }

    private OpenAIClient client() {
        OpenAIClient resolved = client;
        if (resolved == null) {
            synchronized (this) {
                resolved = client;
                if (resolved == null) {
                    resolved = OpenAIOkHttpClient.builder()
                            .apiKey(properties.getContent().getApiKey())
                            .timeout(Duration.ofSeconds(90))
                            .maxRetries(0)
                            .build();
                    client = resolved;
                }
            }
        }
        return resolved;
    }

    private void ensureConfigured() {
        if (properties.getContent().getApiKey() == null || properties.getContent().getApiKey().isBlank()) {
            throw new OpenAIContentGenerationException("OPENAI_API_KEY_MISSING", "OpenAI API key is not configured");
        }
    }

    private static OpenAIContentGenerationException serviceFailure(OpenAIServiceException error) {
        return serviceFailure(error.statusCode());
    }

    private static OpenAIContentGenerationException serviceFailure(int statusCode) {
        String code = switch (statusCode) {
            case 429 -> "HTTP_429";
            case 502 -> "HTTP_502";
            case 503 -> "HTTP_503";
            case 504 -> "HTTP_504";
            case 401, 403 -> "INVALID_API_KEY";
            case 400 -> "OPENAI_VALIDATION_FAILURE";
            default -> "OPENAI_HTTP_" + statusCode;
        };
        return new OpenAIContentGenerationException(code, "OpenAI request failed with HTTP " + statusCode);
    }

    private static String prompt(GenerationRequest request) {
        MarketingStrategyContext strategy = request.strategy();
        return """
                TARGET LANGUAGE
                Code: %s
                Brief: %s

                IMMUTABLE SOURCE
                Reference: %s
                Type: %s
                Title: %s
                Verified facts:
                <verified-facts>
                %s
                </verified-facts>

                APPROVED STRATEGY
                ICP: %s
                ICP goal: %s
                USP: %s
                USP evidence: %s
                Positioning: %s
                Content pillar: %s
                Funnel stage: %s
                Conversion goal: %s
                Primary CTA: %s

                Produce one concise educational draft in the target language. The title, summary, body and CTA
                must be grounded in the source. If the source does not support useful content, do not fill gaps.
                """.formatted(
                request.locale().name(), request.locale().brief(), request.source().sourceReference(),
                request.source().type(), request.facts().title(), request.facts().facts(),
                strategy.icp().name(), strategy.icp().primaryGoal(), strategy.usp().title(),
                strategy.usp().evidenceReference(), strategy.positioning().statement(),
                strategy.contentPillar().name(), strategy.funnelStage().stageKey(),
                strategy.conversionGoal().name(), strategy.conversionGoal().primaryCta());
    }
}
