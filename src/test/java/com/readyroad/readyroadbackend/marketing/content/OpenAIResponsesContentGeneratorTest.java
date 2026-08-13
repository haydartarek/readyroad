package com.readyroad.readyroadbackend.marketing.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.openai.client.OpenAIClient;
import com.openai.errors.OpenAIIoException;
import com.openai.errors.OpenAIInvalidDataException;
import com.openai.models.ResponsesModel;
import com.openai.models.responses.ResponseUsage;
import com.openai.models.responses.StructuredResponse;
import com.openai.models.responses.StructuredResponseCreateParams;
import com.openai.models.responses.StructuredResponseOutputItem;
import com.openai.models.responses.StructuredResponseOutputMessage;
import com.openai.services.blocking.ResponseService;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import java.util.List;
import java.util.Optional;
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
        StructuredResponse<OpenAIStructuredContent> response = mock(StructuredResponse.class);
        StructuredResponseOutputItem<OpenAIStructuredContent> item = mock(StructuredResponseOutputItem.class);
        StructuredResponseOutputMessage<OpenAIStructuredContent> message = mock(StructuredResponseOutputMessage.class);
        StructuredResponseOutputMessage.Content<OpenAIStructuredContent> content =
                mock(StructuredResponseOutputMessage.Content.class);
        ResponseUsage usage = mock(ResponseUsage.class);
        OpenAIStructuredContent output = new OpenAIStructuredContent();
        output.language = "EN";
        output.sourceReference = "ROAD_SIGN:A1";
        output.title = "Verified title";
        output.summary = "Verified summary";
        output.body = "Verified body";
        output.cta = "Continue learning";
        when(client.responses()).thenReturn(responses);
        when(responses.create(any(StructuredResponseCreateParams.class))).thenReturn(response);
        when(response.output()).thenReturn(List.of(item));
        when(item.message()).thenReturn(Optional.of(message));
        when(message.content()).thenReturn(List.of(content));
        when(content.outputText()).thenReturn(Optional.of(output));
        when(response.usage()).thenReturn(Optional.of(usage));
        when(usage.inputTokens()).thenReturn(120L);
        when(usage.outputTokens()).thenReturn(60L);
        when(response.model()).thenReturn(ResponsesModel.ofString("gpt-5.6-terra"));

        var generator = new OpenAIResponsesContentGenerator(properties, client);
        var result = generator.generate(new ContentGenerationClient.GenerationRequest(
                ContentLocale.EN, ContentTestFixtures.source(),
                ContentTestFixtures.source().factsFor(ContentLocale.EN), ContentTestFixtures.strategy()));

        assertThat(result.model()).isEqualTo("gpt-5.6-terra");
        assertThat(result.inputTokens()).isEqualTo(120);
        assertThat(result.outputTokens()).isEqualTo(60);
        ArgumentCaptor<StructuredResponseCreateParams> captor =
                ArgumentCaptor.forClass(StructuredResponseCreateParams.class);
        org.mockito.Mockito.verify(responses).create(captor.capture());
        assertThat(captor.getValue().rawParams().model().orElseThrow().asString()).isEqualTo("gpt-5.6-terra");
        assertThat(captor.getValue().rawParams().store()).contains(false);
        assertThat(captor.getValue().rawParams().reasoning()).isPresent();
        assertThat(captor.getValue().rawParams().reasoning().orElseThrow().effort().orElseThrow().asString())
                .isEqualTo("medium");
    }

    @Test
    void invalidatesTheCachedClientAfterIoFailureSoUnifiedRetryCanReconnect() {
        MarketingProperties properties = configuredProperties();
        OpenAIClient client = mock(OpenAIClient.class);
        ResponseService responses = mock(ResponseService.class);
        when(client.responses()).thenReturn(responses);
        when(responses.create(any(StructuredResponseCreateParams.class)))
                .thenThrow(new OpenAIIoException("test-only I/O failure"));
        var generator = new OpenAIResponsesContentGenerator(properties, client);

        assertThatThrownBy(() -> generator.generate(ContentTestFixtures.generationRequest(ContentLocale.EN)))
                .isInstanceOf(OpenAIContentGenerationException.class)
                .extracting(error -> ((OpenAIContentGenerationException) error).errorCode())
                .isEqualTo("NETWORK_INTERRUPTION");
        assertThat(ReflectionTestUtils.getField(generator, "client")).isNull();
    }

    @Test
    void classifiesInvalidSdkDataAsNonNetworkStructuredOutputFailure() {
        MarketingProperties properties = configuredProperties();
        OpenAIClient client = mock(OpenAIClient.class);
        ResponseService responses = mock(ResponseService.class);
        when(client.responses()).thenReturn(responses);
        when(responses.create(any(StructuredResponseCreateParams.class)))
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
