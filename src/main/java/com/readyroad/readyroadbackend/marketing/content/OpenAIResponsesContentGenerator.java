package com.readyroad.readyroadbackend.marketing.content;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.errors.OpenAIException;
import com.openai.errors.OpenAIIoException;
import com.openai.errors.OpenAIInvalidDataException;
import com.openai.errors.OpenAIServiceException;
import com.openai.models.Reasoning;
import com.openai.models.ReasoningEffort;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseUsage;
import com.openai.models.responses.StructuredResponse;
import com.openai.models.responses.StructuredResponseCreateParams;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContext;
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
    private volatile OpenAIClient client;

    @Autowired
    public OpenAIResponsesContentGenerator(MarketingProperties properties) {
        this(properties, null);
    }

    OpenAIResponsesContentGenerator(MarketingProperties properties, OpenAIClient client) {
        this.properties = properties;
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
            StructuredResponse<OpenAIStructuredContent> response = activeClient.responses().create(params);
            OpenAIStructuredContent output = response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .flatMap(content -> content.outputText().stream())
                    .findFirst()
                    .orElseThrow(() -> new OpenAIContentGenerationException(
                            "MALFORMED_STRUCTURED_OUTPUT", "OpenAI returned no structured content"));
            ResponseUsage usage = response.usage().orElse(null);
            return new GeneratedContent(
                    output.language,
                    output.sourceReference,
                    output.title,
                    output.summary,
                    output.body,
                    output.cta,
                    response.model().asString(),
                    usage == null ? 0 : usage.inputTokens(),
                    usage == null ? 0 : usage.outputTokens(),
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
        String code = switch (error.statusCode()) {
            case 429 -> "HTTP_429";
            case 502 -> "HTTP_502";
            case 503 -> "HTTP_503";
            case 504 -> "HTTP_504";
            case 401, 403 -> "INVALID_API_KEY";
            case 400 -> "OPENAI_VALIDATION_FAILURE";
            default -> "OPENAI_HTTP_" + error.statusCode();
        };
        return new OpenAIContentGenerationException(code, "OpenAI request failed with HTTP " + error.statusCode());
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
