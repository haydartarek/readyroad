package com.readyroad.readyroadbackend.marketing.editorial;

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
import com.readyroad.readyroadbackend.marketing.content.OpenAIContentGenerationException;
import com.readyroad.readyroadbackend.marketing.content.OpenAIRequestFailure;
import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenAIResponsesEditorialTranslationClient implements EditorialTranslationClient {

    private static final String INSTRUCTIONS = """
            You are RijVia's editorial translation and localization adapter.

            Adapt only the supplied canonical article into the requested target language.

            Preserve the exact factual and legal meaning of the canonical source.
            Do not add, remove, broaden, narrow, reinterpret or invent:
            - Belgian traffic rules
            - legal requirements
            - exceptions
            - dates
            - statistics
            - regional claims
            - product capabilities
            - government or official affiliation
            - guarantees
            - testimonials
            - personal experiences
            - source claims

            The result must be a faithful localized adaptation, not a new independently generated article.

            Write naturally for the target audience in Belgium:
            - AR: natural Modern Standard Arabic
            - NL: natural Belgian Dutch
            - FR: natural Belgian French
            - EN: clear natural English

            Preserve the article's level of detail and Markdown structure.
            Preserve headings, lists and emphasis when they exist.
            Do not introduce new links, citations or references.
            Avoid literal machine translation when natural wording can preserve the same meaning.
            Avoid filler, keyword stuffing, emojis and mechanical AI phrasing.

            Copy sourceLanguage, targetLanguage and sourceVersionId exactly from the request.
            Return only the requested structured output.
            """;

    private final MarketingProperties properties;
    private final ObjectMapper objectMapper;
    private volatile OpenAIClient client;

    @Autowired
    public OpenAIResponsesEditorialTranslationClient(
            MarketingProperties properties,
            ObjectMapper objectMapper) {
        this(properties, objectMapper, null);
    }

    OpenAIResponsesEditorialTranslationClient(
            MarketingProperties properties,
            OpenAIClient client) {
        this(properties, new ObjectMapper(), client);
    }

    OpenAIResponsesEditorialTranslationClient(
            MarketingProperties properties,
            ObjectMapper objectMapper,
            OpenAIClient client) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.client = client;
    }

    @Override
    public AdaptedContent adapt(AdaptRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Editorial translation request is required");
        }

        if (request.sourceLocale() == request.targetLocale()) {
            throw new IllegalArgumentException(
                    "Editorial translation source and target languages must differ");
        }

        ensureConfigured();

        StructuredResponseCreateParams<OpenAIEditorialTranslationContent> params =
                ResponseCreateParams.builder()
                        .instructions(INSTRUCTIONS)
                        .input(prompt(request))
                        .reasoning(Reasoning.builder()
                                .effort(ReasoningEffort.of(
                                        properties.getContent().getReasoningEffort()))
                                .build())
                        .maxOutputTokens(
                                properties.getContent().getMaxArticleOutputTokens())
                        .store(false)
                        .text(OpenAIEditorialTranslationContent.class)
                        .model(properties.getContent().getPrimaryModel())
                        .build();

        OpenAIClient activeClient = client();

        try {
            JsonNode response =
                    response(activeClient, params.rawParams());

            String outputText =
                    outputText(response);

            OpenAIEditorialTranslationContent output =
                    parse(outputText);

            return new AdaptedContent(
                    output.sourceLanguage,
                    output.targetLanguage,
                    output.sourceVersionId,
                    output.title,
                    output.slug,
                    output.summary,
                    output.body,
                    output.focusKeyword,
                    output.metaTitle,
                    output.metaDescription,
                    output.cta,
                    response.path("model")
                            .asText(properties.getContent().getPrimaryModel()),
                    response.path("usage")
                            .path("input_tokens")
                            .asLong(0),
                    response.path("usage")
                            .path("output_tokens")
                            .asLong(0),
                    "SUCCEEDED");

        } catch (OpenAIContentGenerationException expected) {
            throw expected;

        } catch (OpenAIServiceException serviceError) {
            throw OpenAIRequestFailure.from(serviceError);

        } catch (OpenAIIoException ioError) {
            invalidate(activeClient);

            throw new OpenAIContentGenerationException(
                    "NETWORK_INTERRUPTION",
                    "OpenAI translation request failed ("
                            + rootCauseType(ioError)
                            + ")");

        } catch (OpenAIInvalidDataException invalidData) {
            throw new OpenAIContentGenerationException(
                    "MALFORMED_STRUCTURED_OUTPUT",
                    "OpenAI returned an invalid translation response");

        } catch (OpenAIException clientError) {
            throw new OpenAIContentGenerationException(
                    "OPENAI_CLIENT_FAILURE",
                    "OpenAI translation client request failed");
        }
    }

    private JsonNode response(
            OpenAIClient activeClient,
            ResponseCreateParams params) {

        try (var raw =
                     activeClient.responses()
                             .withRawResponse()
                             .create(params)) {

            if (raw.statusCode() < 200
                    || raw.statusCode() >= 300) {
                throw OpenAIRequestFailure.from(raw.statusCode(), raw.body(), objectMapper);
            }

            return objectMapper.readTree(raw.body());

        } catch (OpenAIContentGenerationException expected) {
            throw expected;

        } catch (JsonProcessingException invalidBody) {
            throw new OpenAIContentGenerationException(
                    "MALFORMED_STRUCTURED_OUTPUT",
                    "OpenAI returned an unreadable translation response body");

        } catch (IOException interruptedBody) {
            invalidate(activeClient);

            throw new OpenAIContentGenerationException(
                    "NETWORK_INTERRUPTION",
                    "OpenAI translation response body failed ("
                            + rootCauseType(interruptedBody)
                            + ")");
        }
    }

    @Override
    public AdaptedContent adaptKeyword(AdaptRequest request) {
        ensureConfigured();
        var params = ResponseCreateParams.builder()
                .instructions("Translate only the supplied search phrase for the requested Belgian audience. "
                        + "Preserve its search intent. Return one concise focusKeyword of at most 120 characters. "
                        + "Do not write an article or add facts. Copy sourceLanguage, targetLanguage and sourceVersionId exactly.")
                .input("sourceLanguage: " + request.sourceLocale().name()
                        + "\ntargetLanguage: " + request.targetLocale().name()
                        + "\nsourceVersionId: " + request.sourceVersionId()
                        + "\nfocusKeyword: " + request.sourceFocusKeyword())
                .reasoning(Reasoning.builder()
                        .effort(ReasoningEffort.of(properties.getContent().getReasoningEffort())).build())
                .maxOutputTokens(properties.getContent().getMaxOutputTokens())
                .store(false)
                .text(KeywordContent.class)
                .model(properties.getContent().getPrimaryModel())
                .build();
        OpenAIClient activeClient = client();
        try {
            JsonNode result = response(activeClient, params.rawParams());
            KeywordContent keyword = objectMapper.readValue(outputText(result), KeywordContent.class);
            return new AdaptedContent(keyword.sourceLanguage, keyword.targetLanguage, keyword.sourceVersionId,
                    null, null, null, null, keyword.focusKeyword, null, null, null,
                    result.path("model").asText(properties.getContent().getPrimaryModel()),
                    result.path("usage").path("input_tokens").asLong(0),
                    result.path("usage").path("output_tokens").asLong(0), "SUCCEEDED");
        } catch (OpenAIServiceException serviceError) {
            throw OpenAIRequestFailure.from(serviceError);
        } catch (OpenAIIoException ioError) {
            invalidate(activeClient);
            throw new OpenAIContentGenerationException("NETWORK_INTERRUPTION", "OpenAI keyword request was interrupted");
        } catch (JsonProcessingException | OpenAIInvalidDataException invalidData) {
            throw new OpenAIContentGenerationException("MALFORMED_STRUCTURED_OUTPUT", "OpenAI returned an invalid keyword response");
        } catch (OpenAIException clientError) {
            throw new OpenAIContentGenerationException("OPENAI_CLIENT_FAILURE", "OpenAI keyword request failed");
        }
    }

    public static final class KeywordContent {
        public String sourceLanguage;
        public String targetLanguage;
        public long sourceVersionId;
        public String focusKeyword;
    }

    private static String outputText(JsonNode response) {
        for (JsonNode output : response.path("output")) {

            if (!"message".equals(output.path("type").asText())) {
                continue;
            }

            for (JsonNode content : output.path("content")) {

                if ("output_text".equals(content.path("type").asText())
                        && content.path("text").isTextual()) {
                    return content.path("text").asText();
                }
            }
        }

        throw new OpenAIContentGenerationException(
                "MALFORMED_STRUCTURED_OUTPUT",
                "OpenAI returned no structured translation content");
    }

    private OpenAIEditorialTranslationContent parse(
            String outputText) {

        try {
            return objectMapper.readValue(
                    outputText,
                    OpenAIEditorialTranslationContent.class);

        } catch (JsonProcessingException invalidJson) {
            throw new OpenAIContentGenerationException(
                    "MALFORMED_STRUCTURED_OUTPUT",
                    "OpenAI returned invalid structured translation JSON");
        }
    }

    private OpenAIClient client() {
        OpenAIClient resolved = client;

        if (resolved == null) {
            synchronized (this) {
                resolved = client;

                if (resolved == null) {
                    resolved =
                            OpenAIOkHttpClient.builder()
                                    .apiKey(
                                            properties.getContent()
                                                    .getApiKey())
                                    .timeout(
                                            Duration.ofSeconds(90))
                                    .maxRetries(0)
                                    .build();

                    client = resolved;
                }
            }
        }

        return resolved;
    }

    private void invalidate(
            OpenAIClient failedClient) {

        synchronized (this) {
            if (client == failedClient) {
                client = null;
            }
        }
    }

    private void ensureConfigured() {
        if (properties.getContent().getApiKey() == null
                || properties.getContent().getApiKey().isBlank()) {

            throw new OpenAIContentGenerationException(
                    "OPENAI_API_KEY_MISSING",
                    "OpenAI API key is not configured");
        }
    }

    private static String rootCauseType(
            Throwable error) {

        Throwable current = error;

        while (current.getCause() != null
                && current.getCause() != current) {
            current = current.getCause();
        }

        String name =
                current.getClass()
                        .getSimpleName();

        return name.isBlank()
                ? "UnknownIoFailure"
                : name;
    }

    private static String prompt(
            AdaptRequest request) {

        return """
                CANONICAL SOURCE
                Article ID: %d
                Source version ID: %d
                Source language: %s
                Source language guidance: %s

                TARGET
                Target language: %s
                Target language guidance: %s

                CANONICAL TITLE
                <title>
                %s
                </title>

                CANONICAL SLUG
                <slug>
                %s
                </slug>

                CANONICAL SUMMARY
                <summary>
                %s
                </summary>

                CANONICAL FOCUS KEYWORD
                <focus-keyword>
                %s
                </focus-keyword>

                CANONICAL META TITLE
                <meta-title>
                %s
                </meta-title>

                CANONICAL META DESCRIPTION
                <meta-description>
                %s
                </meta-description>

                CANONICAL CTA
                <cta>
                %s
                </cta>

                CANONICAL ARTICLE BODY
                <article-body>
                %s
                </article-body>

                REQUIRED OUTPUT

                Copy:
                sourceLanguage = %s
                targetLanguage = %s
                sourceVersionId = %d

                Adapt title, summary, body, focusKeyword, metaTitle, metaDescription and CTA
                naturally into the target language while preserving exact meaning.

                focusKeyword must be one concise target-language search phrase that preserves
                the canonical search intent. Do not add unrelated queries or keyword stuffing.

                Create a target-language slug suitable for a RijVia article route.
                The slug must:
                - contain no spaces
                - contain no slash
                - contain no query string or fragment
                - use hyphens between words when needed
                - not include a domain
                - remain concise and descriptive

                Do not summarize or reduce the canonical article.
                Do not add facts absent from the canonical article.
                Do not change legal meaning.
                """.formatted(
                request.articleId(),
                request.sourceVersionId(),
                request.sourceLocale().name(),
                request.sourceLocale().brief(),
                request.targetLocale().name(),
                request.targetLocale().brief(),
                safe(request.sourceTitle()),
                safe(request.sourceSlug()),
                safe(request.sourceSummary()),
                safe(request.sourceFocusKeyword()),
                safe(request.sourceMetaTitle()),
                safe(request.sourceMetaDescription()),
                safe(request.sourceCta()),
                safe(request.sourceBody()),
                request.sourceLocale().name(),
                request.targetLocale().name(),
                request.sourceVersionId());
    }

    private static String safe(String value) {
        return value == null
                ? ""
                : value;
    }
}
