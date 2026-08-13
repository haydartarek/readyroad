package com.readyroad.readyroadbackend.marketing.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.openai.client.OpenAIClient;
import com.openai.core.http.HttpResponseFor;
import com.openai.errors.OpenAIIoException;
import com.openai.errors.OpenAIInvalidDataException;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.services.blocking.ResponseService;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import java.io.ByteArrayInputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

class OpenAIResponsesContentGeneratorTest {

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void usesResponsesStructuredOutputWithApprovedModelAndNoServerSideStorage() {
        MarketingProperties properties = new MarketingProperties();
        properties.getContent().setApiKey("test-only-key");
        OpenAIClient client = mock(OpenAIClient.class);
        ResponseService responses = mock(ResponseService.class);
        ResponseService.WithRawResponse rawResponses = mock(ResponseService.WithRawResponse.class);
        HttpResponseFor<Response> response = mock(HttpResponseFor.class);
        when(client.responses()).thenReturn(responses);
        when(responses.withRawResponse()).thenReturn(rawResponses);
        when(rawResponses.create(any(ResponseCreateParams.class))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(new ByteArrayInputStream("""
                {"model":"gpt-5.6-terra","usage":{"input_tokens":120,"output_tokens":60},"output":[
                  {"type":"message","content":[{"type":"output_text","text":
                  "{\\"language\\":\\"EN\\",\\"sourceReference\\":\\"ROAD_SIGN:A1\\",\\"title\\":\\"Verified title\\",\\"summary\\":\\"Verified summary\\",\\"body\\":\\"Verified body\\",\\"cta\\":\\"Continue learning\\"}"}]}
                ]}
                """.getBytes(StandardCharsets.UTF_8)));

        var generator = new OpenAIResponsesContentGenerator(properties, client);
        var result = generator.generate(new ContentGenerationClient.GenerationRequest(
                ContentLocale.EN, ContentTestFixtures.source(),
                ContentTestFixtures.source().factsFor(ContentLocale.EN), ContentTestFixtures.strategy()));

        assertThat(result.model()).isEqualTo("gpt-5.6-terra");
        assertThat(result.inputTokens()).isEqualTo(120);
        assertThat(result.outputTokens()).isEqualTo(60);
        ArgumentCaptor<ResponseCreateParams> captor = ArgumentCaptor.forClass(ResponseCreateParams.class);
        org.mockito.Mockito.verify(rawResponses).create(captor.capture());
        assertThat(captor.getValue().model().orElseThrow().asString()).isEqualTo("gpt-5.6-terra");
        assertThat(captor.getValue().store()).contains(false);
        assertThat(captor.getValue().reasoning()).isPresent();
        assertThat(captor.getValue().reasoning().orElseThrow().effort().orElseThrow().asString())
                .isEqualTo("medium");
    }

    @Test
    void invalidatesTheCachedClientAfterIoFailureSoUnifiedRetryCanReconnect() {
        MarketingProperties properties = configuredProperties();
        OpenAIClient client = mock(OpenAIClient.class);
        ResponseService responses = mock(ResponseService.class);
        ResponseService.WithRawResponse rawResponses = mock(ResponseService.WithRawResponse.class);
        when(client.responses()).thenReturn(responses);
        when(responses.withRawResponse()).thenReturn(rawResponses);
        when(rawResponses.create(any(ResponseCreateParams.class)))
                .thenThrow(new OpenAIIoException("test-only I/O failure", new SocketException("test-only")));
        var generator = new OpenAIResponsesContentGenerator(properties, client);

        Throwable failure = catchThrowable(
                () -> generator.generate(ContentTestFixtures.generationRequest(ContentLocale.EN)));
        assertThat(failure)
                .isInstanceOf(OpenAIContentGenerationException.class)
                .hasMessageContaining("SocketException");
        assertThat(((OpenAIContentGenerationException) failure).errorCode()).isEqualTo("NETWORK_INTERRUPTION");
        assertThat(ReflectionTestUtils.getField(generator, "client")).isNull();
    }

    @Test
    void classifiesInvalidSdkDataAsNonNetworkStructuredOutputFailure() {
        MarketingProperties properties = configuredProperties();
        OpenAIClient client = mock(OpenAIClient.class);
        ResponseService responses = mock(ResponseService.class);
        ResponseService.WithRawResponse rawResponses = mock(ResponseService.WithRawResponse.class);
        when(client.responses()).thenReturn(responses);
        when(responses.withRawResponse()).thenReturn(rawResponses);
        when(rawResponses.create(any(ResponseCreateParams.class)))
                .thenThrow(new OpenAIInvalidDataException("test-only invalid data"));
        var generator = new OpenAIResponsesContentGenerator(properties, client);

        assertThatThrownBy(() -> generator.generate(ContentTestFixtures.generationRequest(ContentLocale.EN)))
                .isInstanceOf(OpenAIContentGenerationException.class)
                .extracting(error -> ((OpenAIContentGenerationException) error).errorCode())
                .isEqualTo("MALFORMED_STRUCTURED_OUTPUT");
    }

    private static MarketingProperties configuredProperties() {
        MarketingProperties properties = new MarketingProperties();
        properties.getContent().setApiKey("test-only-key");
        return properties;
    }
}
