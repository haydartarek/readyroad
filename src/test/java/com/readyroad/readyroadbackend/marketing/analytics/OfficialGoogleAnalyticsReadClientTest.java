package com.readyroad.readyroadbackend.marketing.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class OfficialGoogleAnalyticsReadClientTest {

    @Test
    void buildsTheOfficialPropertyRunReportEndpointWithoutEncodingTheResourceSlash() {
        assertThat(OfficialGoogleAnalyticsReadClient.endpoint("548176182").toString())
                .isEqualTo("https://analyticsdata.googleapis.com/v1beta/properties/548176182:runReport");
    }

    @Test
    void serializesTheJsonNodeAsJsonInsteadOfJacksonNodeImplementationProperties() {
        ObjectMapper objectMapper = new ObjectMapper();
        var client = new OfficialGoogleAnalyticsReadClient(objectMapper, null, null, null);
        var request = objectMapper.createObjectNode();
        request.putArray("metrics").addObject().put("name", "activeUsers");

        assertThat(client.requestBody(request))
                .isEqualTo("{\"metrics\":[{\"name\":\"activeUsers\"}]}")
                .doesNotContain("bigDecimal", "bigInteger", "binary");
    }

    @Test
    void parsesTheRawGoogleResponseWithTheApplicationObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        var client = new OfficialGoogleAnalyticsReadClient(objectMapper, null, null, null);

        assertThat(client.responseBody("{\"rowCount\":1}").path("rowCount").asInt()).isEqualTo(1);
    }
}
