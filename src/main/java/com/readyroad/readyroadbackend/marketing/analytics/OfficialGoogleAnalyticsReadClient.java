package com.readyroad.readyroadbackend.marketing.analytics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
public class OfficialGoogleAnalyticsReadClient implements GoogleAnalyticsReadClient {

    private static final String ENDPOINT = "https://analyticsdata.googleapis.com/v1beta/properties/";
    private static final DateTimeFormatter GA_DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final List<String> METRICS = List.of(
            "activeUsers", "newUsers", "sessions", "screenPageViews", "eventCount", "keyEvents");

    private final ObjectMapper objectMapper;
    private final MarketingProperties properties;
    private final GoogleServiceAccountCredentials credentials;
    private final GoogleReadOnlyHttpClient httpClient;

    @Override
    public AnalyticsModels.Ga4Data fetch(LocalDate startDate, LocalDate endDate) {
        String token = credentials.accessToken();
        JsonNode totals = run(startDate, endDate, List.of("date"), token);
        JsonNode languages = run(startDate, endDate, List.of("date", "language"), token);
        JsonNode devices = run(startDate, endDate, List.of("date", "deviceCategory"), token);
        Map<String, Object> quota = objectMapper.convertValue(
                totals.path("propertyQuota"), objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
        return new AnalyticsModels.Ga4Data(
                rows(totals), rows(languages), rows(devices), Map.copyOf(quota));
    }

    private JsonNode run(LocalDate startDate, LocalDate endDate, List<String> dimensions, String token) {
        var request = objectMapper.createObjectNode();
        request.putArray("dateRanges").addObject()
                .put("startDate", startDate.toString())
                .put("endDate", endDate.toString());
        var dimensionArray = request.putArray("dimensions");
        dimensions.forEach(name -> dimensionArray.addObject().put("name", name));
        var metricArray = request.putArray("metrics");
        METRICS.forEach(name -> metricArray.addObject().put("name", name));
        request.put("limit", "100000");
        request.put("returnPropertyQuota", true);
        try {
            String response = httpClient.client().post()
                    .uri(endpoint(properties.getAnalytics().getGa4PropertyId()))
                    .headers(headers -> headers.setBearerAuth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody(request))
                    .retrieve()
                    .body(String.class);
            return responseBody(response);
        } catch (RestClientResponseException error) {
            throw googleFailure("GA4", error);
        } catch (RuntimeException error) {
            if (error instanceof MarketingTaskExecutionException taskFailure) {
                throw taskFailure;
            }
            throw new MarketingTaskExecutionException("EXTERNAL_API_TEMPORARY_OUTAGE", "GA4 read failed");
        }
    }

    static URI endpoint(String propertyId) {
        return URI.create(ENDPOINT + propertyId + ":runReport");
    }

    String requestBody(JsonNode request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (com.fasterxml.jackson.core.JsonProcessingException error) {
            throw new MarketingTaskExecutionException(
                    "INVALID_ANALYTICS_REQUEST", "GA4 request could not be serialized");
        }
    }

    JsonNode responseBody(String response) {
        try {
            return objectMapper.readTree(response);
        } catch (com.fasterxml.jackson.core.JsonProcessingException | IllegalArgumentException error) {
            throw new MarketingTaskExecutionException(
                    "INVALID_ANALYTICS_RESPONSE", "GA4 response could not be parsed");
        }
    }

    private static MarketingTaskExecutionException googleFailure(String source, RestClientResponseException error) {
        int status = error.getStatusCode().value();
        String code = status == 429 ? "HTTP_429"
                : status == 502 ? "HTTP_502"
                : status == 503 ? "HTTP_503"
                : status == 504 ? "HTTP_504"
                : status == 401 || status == 403 ? "INVALID_CREDENTIALS"
                : "EXTERNAL_API_TEMPORARY_OUTAGE";
        return new MarketingTaskExecutionException(code, source + " read failed with HTTP " + status);
    }

    private static List<AnalyticsModels.MetricRow> rows(JsonNode response) {
        List<String> dimensions = new ArrayList<>();
        response.path("dimensionHeaders").forEach(header -> dimensions.add(header.path("name").asText()));
        List<String> metrics = new ArrayList<>();
        response.path("metricHeaders").forEach(header -> metrics.add(header.path("name").asText()));
        List<AnalyticsModels.MetricRow> rows = new ArrayList<>();
        response.path("rows").forEach(row -> {
            Map<String, String> dimensionValues = new LinkedHashMap<>();
            for (int index = 0; index < dimensions.size(); index++) {
                dimensionValues.put(dimensions.get(index), row.path("dimensionValues").path(index).path("value").asText());
            }
            Map<String, Double> metricValues = new LinkedHashMap<>();
            for (int index = 0; index < metrics.size(); index++) {
                metricValues.put(metrics.get(index), decimal(row.path("metricValues").path(index).path("value").asText()));
            }
            rows.add(new AnalyticsModels.MetricRow(
                    LocalDate.parse(dimensionValues.get("date"), GA_DATE),
                    Map.copyOf(dimensionValues), Map.copyOf(metricValues)));
        });
        return List.copyOf(rows);
    }

    private static double decimal(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
