package com.readyroad.readyroadbackend.marketing.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class OfficialSearchConsoleReadClientTest {

    @Test
    void serializesTheJsonNodeAsJsonInsteadOfJacksonNodeImplementationProperties() {
        ObjectMapper objectMapper = new ObjectMapper();
        var client = new OfficialSearchConsoleReadClient(objectMapper, null, null, null);
        var request = objectMapper.createObjectNode().put("rowLimit", 25_000);

        assertThat(client.requestBody(request))
                .isEqualTo("{\"rowLimit\":25000}")
                .doesNotContain("bigDecimal", "bigInteger", "binary");
    }

    @Test
    void parsesTheRawGoogleResponseWithTheApplicationObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        var client = new OfficialSearchConsoleReadClient(objectMapper, null, null, null);

        assertThat(client.responseBody("{\"rows\":[{\"clicks\":1}]}")
                .path("rows").path(0).path("clicks").asInt()).isEqualTo(1);
    }
}
