package com.readyroad.readyroadbackend.marketing.editorial;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public record EditorialPriorityConfig(
        Map<String, BigDecimal> weights,
        BigDecimal p0,
        BigDecimal p1,
        BigDecimal p2,
        BigDecimal p3,
        BigDecimal missingSearchConsolePercent) {

    static final String SEARCH_DEMAND = "searchDemand";
    static final String SEARCH_CONSOLE = "searchConsoleOpportunity";
    static final String BUSINESS_RELEVANCE = "businessConversionRelevance";
    static final String CONTENT_GAP = "contentGap";
    static final String STRATEGIC_RELEVANCE = "strategicIcpRelevance";
    static final String AUTHORITY = "existingRijViaAuthority";
    static final String LONG_TAIL = "longTailOpportunity";
    static final String MULTILINGUAL = "multilingualOpportunity";
    static final String FRESHNESS = "contentFreshnessNeed";
    static final String INTERNAL_LINKING = "internalLinkingPotential";

    static EditorialPriorityConfig defaults() {
        Map<String, BigDecimal> weights = new LinkedHashMap<>();
        weights.put(SEARCH_DEMAND, BigDecimal.valueOf(20));
        weights.put(SEARCH_CONSOLE, BigDecimal.valueOf(20));
        weights.put(BUSINESS_RELEVANCE, BigDecimal.valueOf(15));
        weights.put(CONTENT_GAP, BigDecimal.valueOf(10));
        weights.put(STRATEGIC_RELEVANCE, BigDecimal.valueOf(10));
        weights.put(AUTHORITY, BigDecimal.valueOf(5));
        weights.put(LONG_TAIL, BigDecimal.valueOf(5));
        weights.put(MULTILINGUAL, BigDecimal.valueOf(5));
        weights.put(FRESHNESS, BigDecimal.valueOf(5));
        weights.put(INTERNAL_LINKING, BigDecimal.valueOf(5));
        return new EditorialPriorityConfig(
                Map.copyOf(weights),
                BigDecimal.valueOf(80),
                BigDecimal.valueOf(60),
                BigDecimal.valueOf(40),
                BigDecimal.ZERO,
                BigDecimal.valueOf(50));
    }
}
