package com.readyroad.readyroadbackend.marketing.editorial;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class EditorialPerformanceDtos {

    private EditorialPerformanceDtos() {}

    public record Metrics(
            double clicks,
            double impressions,
            double ctr,
            double averagePosition) {}

    public record Snapshot(
            long id,
            long articleId,
            long publicationId,
            String language,
            String publishedPath,
            LocalDate periodStart,
            LocalDate periodEnd,
            Metrics current,
            Metrics previous,
            String evidenceState,
            String indexingState,
            Instant createdAt) {}

    public record Recommendation(
            long id,
            boolean recommended,
            List<String> reasonCodes,
            Map<String, Object> evidence,
            LocalDate periodEnd,
            Instant createdAt) {}

    public record Overview(
            List<Snapshot> latestSnapshots,
            Recommendation latestRecommendation) {}
}
