package com.readyroad.readyroadbackend.marketing.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyReadService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RijViaSeoOpportunityEngineTest {

    private final RijViaSeoOpportunityEngine engine = new RijViaSeoOpportunityEngine(
            new OrganicDiscoveryClassifier(mock(MarketingStrategyReadService.class)));

    @Test
    void separatesLegacyBrandRiskAndBuildsPathPreservingRijViaMigrationEvidence() {
        SearchConsoleWorkbookParser.ParsedWorkbook workbook = workbook();

        RijViaSeoOpportunityEngine.Analysis result = engine.analyze(workbook, "https://rijvia.be");

        assertThat(result.queryRows()).anySatisfy(row -> {
            assertThat(row.metric().dimension()).isEqualTo("readyroad");
            assertThat(row.brandClassification()).isEqualTo("LEGACY_BRAND_QUERY");
            assertThat(row.state()).isEqualTo("MIGRATION_RISK");
            assertThat(row.priority()).isEqualTo("P0");
        });
        assertThat(result.queryRows()).anySatisfy(row -> {
            assertThat(row.metric().dimension()).contains("autoweg");
            assertThat(row.language()).isEqualTo("NL");
            assertThat(row.state()).isEqualTo("CONTENT_GAP");
        });

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> mappings =
                (List<Map<String, Object>>) result.report().get("urlMappings");
        assertThat(mappings).anySatisfy(mapping -> {
            assertThat(mapping.get("oldUrl")).isEqualTo("https://readyroad.be/fr");
            assertThat(mapping.get("newUrlCandidate")).isEqualTo("https://rijvia.be/fr");
            assertThat(mapping.get("routeExists")).isEqualTo(true);
            @SuppressWarnings("unchecked")
            Map<String, String> hreflang = (Map<String, String>) mapping.get("hreflangExpected");
            assertThat(hreflang)
                    .containsEntry("x-default", "https://rijvia.be/")
                    .containsEntry("fr-BE", "https://rijvia.be/fr");
        });

        @SuppressWarnings("unchecked")
        Map<String, Object> totals = (Map<String, Object>) result.report().get("propertyTotals");
        assertThat(totals.get("clicks")).isEqualTo(38d);
        assertThat(totals.get("impressions")).isEqualTo(4867d);
        assertThat(result.draftBriefs()).extracting(RijViaSeoOpportunityEngine.DraftBrief::language)
                .contains("NL", "FR");
        assertThat(result.report().get("ownerDecisionsRequired").toString())
                .doesNotContain("canonical production domain", "release window");
    }

    private static SearchConsoleWorkbookParser.ParsedWorkbook workbook() {
        List<SearchConsoleWorkbookParser.MetricRow> queries = List.of(
                new SearchConsoleWorkbookParser.MetricRow("readyroad", 12, 38, 12d / 38d, 3),
                new SearchConsoleWorkbookParser.MetricRow(
                        "autoweg autosnelweg verschil belgië", 0, 100, 0, 12));
        List<SearchConsoleWorkbookParser.MetricRow> pages = List.of(
                new SearchConsoleWorkbookParser.MetricRow(
                        "https://readyroad.be/fr", 1, 1158, 0.0009, 5.01),
                new SearchConsoleWorkbookParser.MetricRow(
                        "https://readyroad.be/nl/traffic-signs/A13", 0, 189, 0, 8.38));
        return new SearchConsoleWorkbookParser.ParsedWorkbook(
                "search-console.xlsx", "a".repeat(64), 2048,
                LocalDate.of(2026, 8, 18), LocalDate.of(2026, 8, 19),
                queries, pages, List.of(), List.of(), List.of(),
                List.of(
                        new SearchConsoleWorkbookParser.ChartRow(LocalDate.of(2026, 8, 18), 20, 2500, .008, 9),
                        new SearchConsoleWorkbookParser.ChartRow(LocalDate.of(2026, 8, 19), 18, 2367, .0076, 9.4)),
                Map.of("نوع البحث", "الويب"), List.of(), 0);
    }
}
