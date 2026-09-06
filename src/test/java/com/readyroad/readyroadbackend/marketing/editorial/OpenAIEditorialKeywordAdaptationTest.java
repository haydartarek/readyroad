package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.core.http.HttpResponseFor;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.services.blocking.ResponseService;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.content.ContentLocale;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class OpenAIEditorialKeywordAdaptationTest {
    @Test
    @SuppressWarnings("unchecked")
    void repairsOnlyTheKeywordWithoutSendingOrGeneratingTheArticle() throws Exception {
        var properties = new MarketingProperties();
        properties.getContent().setApiKey("test-only-key");
        var client = mock(OpenAIClient.class);
        var responses = mock(ResponseService.class);
        var raw = mock(ResponseService.WithRawResponse.class);
        HttpResponseFor<Response> http = mock(HttpResponseFor.class);
        when(client.responses()).thenReturn(responses);
        when(responses.withRawResponse()).thenReturn(raw);
        when(raw.create(any(ResponseCreateParams.class))).thenReturn(http);
        when(http.statusCode()).thenReturn(200);
        var mapper = new ObjectMapper();
        var keyword = mapper.createObjectNode().put("sourceLanguage", "EN").put("targetLanguage", "NL")
                .put("sourceVersionId", 12).put("focusKeyword", "theorie-examen oefenen");
        var result = mapper.createObjectNode().put("model", "configured-model");
        result.putObject("usage").put("input_tokens", 85).put("output_tokens", 40);
        result.putArray("output").addObject().put("type", "message").putArray("content")
                .addObject().put("type", "output_text").put("text", keyword.toString());
        when(http.body()).thenReturn(new ByteArrayInputStream(mapper.writeValueAsBytes(result)));
        var request = new EditorialTranslationClient.AdaptRequest(1, 12, ContentLocale.EN, ContentLocale.NL,
                "Article title", "article-slug", "Summary", "COMPLETE_ARTICLE_MUST_NOT_BE_SENT",
                "driving theory practice", "Meta title", "Meta description", "Start learning");

        var translation = new OpenAIResponsesEditorialTranslationClient(properties, client).adaptKeyword(request);

        var params = ArgumentCaptor.forClass(ResponseCreateParams.class);
        verify(raw).create(params.capture());
        assertThat(params.getValue().input().orElseThrow().asText())
                .contains("driving theory practice").doesNotContain("COMPLETE_ARTICLE_MUST_NOT_BE_SENT");
        assertThat(params.getValue().maxOutputTokens()).contains(properties.getContent().getMaxOutputTokens());
        assertThat(params.getValue().store()).contains(false);
        assertThat(translation.body()).isNull();
        assertThat(translation.inputTokens()).isEqualTo(85);
        assertThat(translation.outputTokens()).isEqualTo(40);
        assertThat(new EditorialTranslationQualityPolicy(properties).validateKeyword(request, translation).focusKeyword())
                .isEqualTo("theorie-examen oefenen");
    }
}
