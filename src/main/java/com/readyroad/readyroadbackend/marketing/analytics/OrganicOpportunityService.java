package com.readyroad.readyroadbackend.marketing.analytics;

import java.net.URI;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganicOpportunityService {

    private static final Set<String> DECLINE_ELIGIBLE = Set.of("EMERGING", "OPPORTUNITY", "ESTABLISHED");

    private final AnalyticsStore store;
    private final AnalyticsSettingsService settingsService;
    private final OrganicDiscoveryClassifier classifier;

    @Transactional
    public int analyze(LocalDate completedThrough) {
        AnalyticsSettings settings = settingsService.current();
        LocalDate currentStart = completedThrough.minusDays(settings.windowDays() - 1L);
        LocalDate previousEnd = currentStart.minusDays(1);
        LocalDate previousStart = previousEnd.minusDays(settings.windowDays() - 1L);
        Map<String, AnalyticsModels.QueryAggregate> previous = store.aggregateQueryMap(previousStart, previousEnd);
        int analyzed = 0;
        for (AnalyticsModels.QueryAggregate current : store.aggregateQueries(currentStart, completedThrough)) {
            String mapKey = AnalyticsStore.queryKey(current.query(), current.page(), current.language());
            AnalyticsModels.QueryAggregate prior = previous.get(mapKey);
            String opportunityKey = AnalyticsStore.opportunityKey(
                    current.query(), current.page(), current.language());
            String previousState = store.currentOpportunityState(opportunityKey);
            AnalyticsModels.QueryAggregate enriched = new AnalyticsModels.QueryAggregate(
                    current.query(), current.page(), current.language(), current.brandClassification(),
                    current.longTail(), current.searchIntent(), current.clicks(), current.impressions(),
                    current.ctr(), current.averagePosition(), prior == null ? null : prior.clicks(),
                    prior == null ? null : prior.ctr(), prior == null ? null : prior.averagePosition(),
                    previousState, current.distinctPages());
            AnalyticsModels.OpportunityDecision decision = decide(enriched, settings);
            store.saveOpportunity(enriched, decision);
            if (decision.state() == AnalyticsModels.OpportunityState.OPPORTUNITY
                    && isHomepage(enriched.page())) {
                store.saveContentGap(enriched, Map.of(
                        "reason", "OPPORTUNITY_QUERY_LANDS_ON_HOMEPAGE",
                        "impressions", enriched.impressions(),
                        "averagePosition", enriched.averagePosition()));
            }
            analyzed++;
        }
        return analyzed;
    }

    AnalyticsModels.OpportunityDecision decide(
            AnalyticsModels.QueryAggregate query,
            AnalyticsSettings settings) {
        boolean relevance = classifier.relevant(query.query());
        boolean cannibalization = query.distinctPages() > 1;
        boolean hasHistory = query.previousPosition() != null
                && query.previousClicks() != null
                && query.previousCtr() != null;
        double positionChange = hasHistory ? query.averagePosition() - query.previousPosition() : 0;
        double clicksDecline = declinePercent(query.previousClicks(), query.clicks());
        double ctrDecline = declinePercent(query.previousCtr(), query.ctr());
        boolean importantDecline = hasHistory && (
                positionChange >= settings.positionDecline()
                        || clicksDecline >= settings.clicksDeclinePercent()
                        || ctrDecline >= settings.ctrDeclinePercent());
        AnalyticsModels.Trend trend = trend(hasHistory, positionChange, clicksDecline, ctrDecline, settings);
        boolean intentMatches = "INFORMATIONAL".equals(query.searchIntent())
                || "TRANSACTIONAL".equals(query.searchIntent());
        boolean previouslyQualified = query.previousState() != null
                && DECLINE_ELIGIBLE.contains(query.previousState());

        AnalyticsModels.OpportunityState state;
        if (previouslyQualified && importantDecline) {
            state = AnalyticsModels.OpportunityState.DECLINING;
        } else if (hasHistory
                && query.averagePosition() <= settings.establishedPositionMax()
                && query.clicks() >= settings.establishedClicks()
                && (settings.stableWindows() <= 2 || "ESTABLISHED".equals(query.previousState()))
                && !importantDecline) {
            state = AnalyticsModels.OpportunityState.ESTABLISHED;
        } else if (query.impressions() >= settings.opportunityImpressions()
                && between(query.averagePosition(), settings.opportunityPositionMin(), settings.opportunityPositionMax())
                && relevance && intentMatches && !cannibalization && !query.page().isBlank()) {
            state = AnalyticsModels.OpportunityState.OPPORTUNITY;
        } else if (query.impressions() >= settings.emergingImpressions()
                && between(query.averagePosition(), settings.emergingPositionMin(), settings.emergingPositionMax())
                && relevance
                && (trend == AnalyticsModels.Trend.STABLE || trend == AnalyticsModels.Trend.IMPROVING)) {
            state = AnalyticsModels.OpportunityState.EMERGING;
        } else {
            state = AnalyticsModels.OpportunityState.DISCOVERING;
        }

        Map<String, Object> evidence = new LinkedHashMap<>();
        evidence.put("windowDays", settings.windowDays());
        evidence.put("hasHistoricalComparison", hasHistory);
        evidence.put("positionChange", positionChange);
        evidence.put("clicksDeclinePercent", clicksDecline);
        evidence.put("ctrDeclinePercent", ctrDecline);
        evidence.put("searchIntentMatch", intentMatches);
        evidence.put("strategyRelevance", relevance);
        evidence.put("distinctPages", query.distinctPages());
        evidence.put("volumeQualified", query.impressions() >= settings.emergingImpressions());
        return new AnalyticsModels.OpportunityDecision(
                state, trend, relevance, cannibalization, Map.copyOf(evidence));
    }

    private static AnalyticsModels.Trend trend(
            boolean hasHistory,
            double positionChange,
            double clicksDecline,
            double ctrDecline,
            AnalyticsSettings settings) {
        if (!hasHistory) {
            return AnalyticsModels.Trend.INSUFFICIENT_DATA;
        }
        if (positionChange >= settings.positionDecline()
                || clicksDecline >= settings.clicksDeclinePercent()
                || ctrDecline >= settings.ctrDeclinePercent()) {
            return AnalyticsModels.Trend.DECLINING;
        }
        if (positionChange <= -1 || clicksDecline <= -10 || ctrDecline <= -10) {
            return AnalyticsModels.Trend.IMPROVING;
        }
        return AnalyticsModels.Trend.STABLE;
    }

    private static double declinePercent(Double previous, double current) {
        if (previous == null || previous <= 0) {
            return 0;
        }
        return ((previous - current) / previous) * 100.0;
    }

    private static boolean between(double value, double minimum, double maximum) {
        return value >= minimum && value <= maximum;
    }

    private static boolean isHomepage(String page) {
        if (page == null || page.isBlank()) {
            return false;
        }
        try {
            String path = URI.create(page).getPath();
            return path == null || path.equals("/") || path.matches("/(ar|nl|fr)/?");
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}
