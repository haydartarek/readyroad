package com.readyroad.readyroadbackend.marketing.analytics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.net.URLEncoder;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
public class OfficialSearchConsoleReadClient implements SearchConsoleReadClient {

    private static final int PAGE_SIZE = 25_000;
    private static final String ENDPOINT =
            "https://www.googleapis.com/webmasters/v3/sites/{siteUrl}/searchAnalytics/query";

    private final ObjectMapper objectMapper;
    private final MarketingProperties properties;
    private final GoogleServiceAccountCredentials credentials;
    private final GoogleReadOnlyHttpClient httpClient;

    @Override
    public AnalyticsModels.SearchConsoleData fetch(LocalDate startDate, LocalDate endDate) {
        String token = credentials.accessToken();
        List<AnalyticsModels.SearchRow> totals = query(startDate, endDate, List.of("date"), "byProperty", token);
        List<AnalyticsModels.SearchRow> queries = query(
                startDate, endDate, List.of("date", "query", "page"), "auto", token);
        List<AnalyticsModels.SearchRow> pages = query(
                startDate, endDate, List.of("date", "page", "device"), "auto", token);
        return new AnalyticsModels.SearchConsoleData(
                totals, queries, pages,
                Map.of("propertyRows", totals.size(), "queryRows", queries.size(), "pageRows", pages.size()));
    }

    private List<AnalyticsModels.SearchRow> query(
            LocalDate startDate,
            LocalDate endDate,
            List<String> dimensions,
            String aggregation,
            String token) {
        List<AnalyticsModels.SearchRow> all = new ArrayList<>();
        int startRow = 0;
        while (true) {
            JsonNode response = execute(startDate, endDate, dimensions, aggregation, startRow, token);
            int before = all.size();
            response.path("rows").forEach(row -> all.add(map(row, dimensions)));
            int received = all.size() - before;
            if (received < PAGE_SIZE) {
                return List.copyOf(all);
            }
            startRow += PAGE_SIZE;
        }
    }

    private JsonNode execute(
            LocalDate startDate,
            LocalDate endDate,
            List<String> dimensions,
            String aggregation,
            int startRow,
            String token) {
        var request = objectMapper.createObjectNode()
                .put("startDate", startDate.toString())
                .put("endDate", endDate.toString())
                .put("type", "web")
                .put("dataState", "final")
                .put("aggregationType", aggregation)
                .put("rowLimit", PAGE_SIZE)
                .put("startRow", startRow);
        var dimensionArray = request.putArray("dimensions");
        dimensions.forEach(dimensionArray::add);
        String siteUrl = URLEncoder.encode(
                properties.getAnalytics().getSearchConsoleSiteUrl(), StandardCharsets.UTF_8);
        try {
            URI endpoint = URI.create(ENDPOINT.replace("{siteUrl}", siteUrl));
            String response = httpClient.client().post()
                    .uri(endpoint)
                    .headers(headers -> headers.setBearerAuth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody(request))
                    .retrieve()
                    .body(String.class);
            return responseBody(response);
        } catch (RestClientResponseException error) {
            int status = error.getStatusCode().value();
            String code = status == 429 ? "HTTP_429"
                    : status == 502 ? "HTTP_502"
                    : status == 503 ? "HTTP_503"
                    : status == 504 ? "HTTP_504"
                    : status == 401 || status == 403 ? "INVALID_CREDENTIALS"
                    : "EXTERNAL_API_TEMPORARY_OUTAGE";
            throw new MarketingTaskExecutionException(code, "Search Console read failed with HTTP " + status);
        } catch (RuntimeException error) {
            if (error instanceof MarketingTaskExecutionException taskFailure) {
                throw taskFailure;
            }
            throw new MarketingTaskExecutionException("EXTERNAL_API_TEMPORARY_OUTAGE", "Search Console read failed");
        }
    }

    String requestBody(JsonNode request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (com.fasterxml.jackson.core.JsonProcessingException error) {
            throw new MarketingTaskExecutionException(
                    "INVALID_ANALYTICS_REQUEST", "Search Console request could not be serialized");
        }
    }

    JsonNode responseBody(String response) {
        try {
            return objectMapper.readTree(response);
        } catch (com.fasterxml.jackson.core.JsonProcessingException | IllegalArgumentException error) {
            throw new MarketingTaskExecutionException(
                    "INVALID_ANALYTICS_RESPONSE", "Search Console response could not be parsed");
        }
    }

    private static AnalyticsModels.SearchRow map(JsonNode row, List<String> dimensions) {
        LocalDate date = null;
        String query = "";
        String page = "";
        String device = "UNKNOWN";
        for (int index = 0; index < dimensions.size(); index++) {
            String value = row.path("keys").path(index).asText();
            switch (dimensions.get(index)) {
                case "date" -> date = LocalDate.parse(value);
                case "query" -> query = value;
                case "page" -> page = value;
                case "device" -> device = value.toUpperCase();
                default -> { }
            }
        }
        return new AnalyticsModels.SearchRow(
                date, query, page, device,
                row.path("clicks").asDouble(), row.path("impressions").asDouble(),
                row.path("ctr").asDouble(), row.path("position").asDouble());
    }
}
