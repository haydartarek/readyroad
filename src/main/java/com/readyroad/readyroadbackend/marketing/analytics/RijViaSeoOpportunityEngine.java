package com.readyroad.readyroadbackend.marketing.analytics;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class RijViaSeoOpportunityEngine {

    private static final Pattern ARABIC = Pattern.compile("[\\p{InArabic}]");
    private static final Pattern DYNAMIC_LESSON = Pattern.compile("^/lessons/[^/]+(?:/\\d+)?$");
    private static final Pattern DYNAMIC_SIGN = Pattern.compile(
            "^/traffic-signs/[^/]+(?:/(?:practice|exam)(?:/\\d+)?)?$");
    private static final Set<String> STATIC_ROUTES = Set.of(
            "/", "/about", "/contact", "/cookie-policy", "/disclaimer", "/faq",
            "/lessons", "/privacy", "/privacy-policy", "/terms", "/traffic-signs", "/videos",
            "/login", "/register", "/forgot-password", "/exam", "/practice", "/practice/random");

    private final OrganicDiscoveryClassifier baseClassifier;

    public RijViaSeoOpportunityEngine(OrganicDiscoveryClassifier baseClassifier) {
        this.baseClassifier = baseClassifier;
    }

    public Analysis analyze(SearchConsoleWorkbookParser.ParsedWorkbook workbook, String candidateDomain) {
        List<AnalyzedRow> queryRows = workbook.queries().stream()
                .map(this::analyzeQuery)
                .toList();
        List<AnalyzedRow> pageRows = workbook.pages().stream()
                .map(this::analyzePage)
                .toList();
        List<Map<String, Object>> urlMap = pageRows.stream()
                .map(row -> migrationMap(row.metric().dimension(), candidateDomain))
                .toList();
        List<Map<String, Object>> internalLinks = internalLinks(workbook.pages());
        List<DraftBrief> briefs = draftBriefs(workbook);

        List<Map<String, Object>> ranked = new ArrayList<>();
        queryRows.forEach(row -> ranked.add(row.asReport("QUERY")));
        pageRows.forEach(row -> ranked.add(row.asReport("PAGE")));
        ranked.sort(Comparator
                .comparingInt((Map<String, Object> value) -> priorityOrder((String) value.get("priority")))
                .thenComparing(value -> -((Number) value.get("score")).intValue())
                .thenComparing(value -> -((Number) value.get("impressions")).doubleValue())
                .thenComparing(value -> String.valueOf(value.get("dimension"))));

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("propertyTotals", propertyTotals(workbook));
        report.put("dimensionTotals", dimensionTotals(workbook));
        report.put("languageBreakdown", languageBreakdown(pageRows));
        report.put("deviceBreakdown", metricMaps(workbook.devices()));
        report.put("countryBreakdown", metricMaps(workbook.countries()));
        report.put("searchAppearance", metricMaps(workbook.searchAppearance()));
        report.put("topRisks", ranked.stream().filter(row -> Set.of(
                        "MIGRATION_RISK", "TECHNICAL_SEO_RISK", "CTR_REPAIR")
                        .contains(row.get("state"))).limit(30).toList());
        report.put("topOpportunities", ranked.stream().filter(row -> !Set.of(
                        "MIGRATION_RISK", "TECHNICAL_SEO_RISK", "LOW_CONFIDENCE")
                        .contains(row.get("state"))).limit(50).toList());
        report.put("urlMappings", urlMap);
        report.put("blockedMappings", urlMap.stream().filter(row -> !Boolean.TRUE.equals(row.get("routeExists"))).toList());
        report.put("internalLinkOpportunities", internalLinks);
        report.put("draftBriefs", briefs.stream().map(DraftBrief::asReport).toList());
        report.put("technicalSeo", technicalSeo(urlMap));
        report.put("authority", Map.of(
                "mode", "FREE_OR_EARNED_ONLY",
                "candidateImportAvailable", false,
                "outreach", "DISABLED",
                "paidBacklinks", "REJECTED"));
        report.put("social", Map.of(
                "officialHandlesConfigured", false,
                "draftOnly", true,
                "publishing", "DISABLED",
                "ownerDecisionRequired", true));
        report.put("confidenceNotes", List.of(
                "LOW confidence is used for fewer than 10 impressions.",
                "Dimension totals are directional and are not summed into a false site total.",
                "A single export cannot prove ranking stability or conversion performance."));
        report.put("ownerDecisionsRequired", List.of(
                "Confirm official RijVia social handles before any publishing integration.",
                "Approve or reject each local content brief before drafting or publishing."));

        return new Analysis(queryRows, pageRows, briefs, Map.copyOf(report));
    }

    private AnalyzedRow analyzeQuery(SearchConsoleWorkbookParser.MetricRow row) {
        String normalized = normalize(row.dimension());
        String language = queryLanguage(normalized);
        String brand = baseClassifier.brand(row.dimension()).name();
        LinkedHashSet<String> classes = commonClassifications(row, language, brand);
        if (containsAny(normalized, "traffic sign", "road sign", "verkeersbord", "verbodsbord", "panneau", "علامة", "إشارة")) {
            classes.add("TRAFFIC_SIGN_QUERY");
        }
        if (containsAny(normalized, "theory exam", "theorie examen", "examen théorique", "امتحان السياقة النظري", "اختبار نظري")) {
            classes.add("THEORY_EXAM_QUERY");
        }
        if (containsAny(normalized, "practical exam", "praktijkexamen", "examen pratique", "امتحان عملي")) {
            classes.add("PRACTICAL_EXAM_QUERY");
        }
        if (containsAny(normalized, "belgium", "belgië", "belgique", "بلجيكا")) {
            classes.add("LOCAL_BELGIUM_QUERY");
        }

        String state;
        if ("OLD_BRAND_READYROAD".equals(brand) || "COMPETITOR_OR_AMBIGUOUS_BRAND".equals(brand)) {
            state = "MIGRATION_RISK";
        } else if (row.impressions() < 5) {
            state = "LOW_CONFIDENCE";
        } else if (row.impressions() >= 50 && row.clicks() == 0) {
            state = "CONTENT_GAP";
        } else if (row.impressions() >= 50 && between(row.position(), 4, 20)) {
            state = "OPPORTUNITY";
        } else if (row.impressions() >= 20 && between(row.position(), 11, 30)) {
            state = "EMERGING";
        } else if (row.position() <= 10 && row.clicks() >= 10) {
            state = "ESTABLISHED";
        } else {
            state = "DISCOVERING";
        }
        return analyzed(row, language, brand, classes, state, false);
    }

    private AnalyzedRow analyzePage(SearchConsoleWorkbookParser.MetricRow row) {
        String url = row.dimension();
        String language = baseClassifier.language(url, "");
        String brand = isReadyRoadUrl(url) ? "OLD_BRAND_READYROAD" : "NON_BRAND";
        LinkedHashSet<String> classes = commonClassifications(row, language, brand);
        classes.add("MIGRATION_SOURCE_URL");
        String path = path(url);
        if (path.contains("/traffic-signs")) {
            classes.add("TRAFFIC_SIGN_PAGE");
        } else if (path.contains("/lessons")) {
            classes.add("LESSON_PAGE");
        } else if (path.endsWith("/faq")) {
            classes.add("FAQ_PAGE");
        } else if (stripLocale(path).equals("/")) {
            classes.add("LANDING_PAGE");
        }

        String state;
        if (url.toLowerCase(Locale.ROOT).startsWith("http://") || !routeExists(path)) {
            state = "TECHNICAL_SEO_RISK";
        } else if (row.impressions() < 5) {
            state = "LOW_CONFIDENCE";
        } else if (row.impressions() >= 100 && row.ctr() < 0.02 && row.position() <= 20) {
            state = "CTR_REPAIR";
        } else if (row.impressions() >= 50 && row.clicks() == 0 && between(row.position(), 4, 30)) {
            state = "INTERNAL_LINK_GAP";
        } else if (row.impressions() >= 50 && between(row.position(), 4, 20)) {
            state = "OPPORTUNITY";
        } else if (row.impressions() >= 20 && between(row.position(), 11, 30)) {
            state = "EMERGING";
        } else if (row.position() <= 10 && row.clicks() >= 10) {
            state = "ESTABLISHED";
        } else {
            state = "DISCOVERING";
        }
        return analyzed(row, language, brand, classes, state, true);
    }

    private LinkedHashSet<String> commonClassifications(
            SearchConsoleWorkbookParser.MetricRow row, String language, String brand) {
        LinkedHashSet<String> classes = new LinkedHashSet<>();
        classes.add(brand);
        AnalyticsModels.SearchIntent intent = baseClassifier.intent(row.dimension());
        classes.add(intent.name());
        if (baseClassifier.longTail(row.dimension())) {
            classes.add("LONG_TAIL");
        }
        if (row.impressions() < 10) {
            classes.add("LOW_CONFIDENCE_SAMPLE");
        }
        classes.add("LANGUAGE_" + language);
        return classes;
    }

    private AnalyzedRow analyzed(
            SearchConsoleWorkbookParser.MetricRow row,
            String language,
            String brand,
            Set<String> classifications,
            String state,
            boolean page) {
        int score = score(row, language, brand, classifications, state, page);
        String priority = priority(state, score);
        String action = action(state, classifications, page);
        String confidence = confidence(row.impressions());
        boolean relevant = classifications.stream().anyMatch(value -> Set.of(
                "TRAFFIC_SIGN_QUERY", "THEORY_EXAM_QUERY", "PRACTICAL_EXAM_QUERY",
                "TRAFFIC_SIGN_PAGE", "LESSON_PAGE", "FAQ_PAGE", "LANDING_PAGE")
                .contains(value));
        return new AnalyzedRow(
                row, language, brand, List.copyOf(classifications), state, priority,
                action, confidence, score, relevant);
    }

    private int score(
            SearchConsoleWorkbookParser.MetricRow row,
            String language,
            String brand,
            Set<String> classifications,
            String state,
            boolean page) {
        double volume = Math.min(30, Math.log10(row.impressions() + 1) * 10);
        double position = row.position() <= 3 ? 18 : row.position() <= 10 ? 15 : row.position() <= 20 ? 10 : 4;
        double ctrGap = row.impressions() >= 20 && row.ctr() < 0.02 ? 15 : row.ctr() < 0.05 ? 8 : 2;
        double belgium = classifications.contains("LOCAL_BELGIUM_QUERY") || page ? 8 : 4;
        double languageOpportunity = Set.of("AR", "NL", "FR").contains(language) ? 8 : 3;
        double contentType = classifications.stream().anyMatch(value -> value.endsWith("_QUERY") || value.endsWith("_PAGE")) ? 7 : 2;
        double conversion = classifications.contains("TRANSACTIONAL") ? 5 : 2;
        double internalLinking = page && row.impressions() >= 20 ? 5 : 2;
        double brandRisk = Set.of("OLD_BRAND_READYROAD", "COMPETITOR_OR_AMBIGUOUS_BRAND").contains(brand) ? 15 : 0;
        double technicalRisk = Set.of("MIGRATION_RISK", "TECHNICAL_SEO_RISK").contains(state) ? 10 : 0;
        double confidenceFactor = row.impressions() < 5 ? 0.45 : row.impressions() < 10 ? 0.7 : 1;
        return (int) Math.round(Math.min(100, (volume + position + ctrGap + belgium
                + languageOpportunity + contentType + conversion + internalLinking + brandRisk + technicalRisk)
                * confidenceFactor));
    }

    private static String priority(String state, int score) {
        if (Set.of("MIGRATION_RISK", "TECHNICAL_SEO_RISK").contains(state) || score >= 75) {
            return "P0";
        }
        if (score >= 55) {
            return "P1";
        }
        if (score >= 30) {
            return "P2";
        }
        return "P3";
    }

    private static String action(String state, Set<String> classifications, boolean page) {
        return switch (state) {
            case "MIGRATION_RISK" -> "MONITOR_OLD_AND_AMBIGUOUS_BRAND";
            case "TECHNICAL_SEO_RISK" -> "REPAIR_URL_OR_ROUTE_MAPPING";
            case "CTR_REPAIR" -> "REFINE_LOCALIZED_TITLE_AND_META";
            case "CONTENT_GAP" -> "PREPARE_EVIDENCE_BACKED_CONTENT_BRIEF";
            case "INTERNAL_LINK_GAP" -> "ADD_RELEVANT_SAME_LANGUAGE_INTERNAL_LINKS";
            case "LOW_CONFIDENCE" -> "COLLECT_MORE_EVIDENCE";
            default -> page
                    ? "MONITOR_PAGE_AND_INTERNAL_LINK_SUPPORT"
                    : classifications.contains("TRAFFIC_SIGN_QUERY")
                            ? "SUPPORT_TRAFFIC_SIGN_CLUSTER"
                            : "MONITOR_QUERY_OPPORTUNITY";
        };
    }

    private static String confidence(double impressions) {
        if (impressions >= 50) {
            return "HIGH";
        }
        if (impressions >= 10) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private static Map<String, Object> propertyTotals(SearchConsoleWorkbookParser.ParsedWorkbook workbook) {
        double clicks = workbook.chart().stream().mapToDouble(SearchConsoleWorkbookParser.ChartRow::clicks).sum();
        double impressions = workbook.chart().stream().mapToDouble(SearchConsoleWorkbookParser.ChartRow::impressions).sum();
        double weightedPosition = workbook.chart().stream()
                .mapToDouble(row -> row.position() * row.impressions()).sum();
        return Map.of(
                "source", "DAILY_PROPERTY_CHART",
                "periodStart", workbook.periodStart(),
                "periodEnd", workbook.periodEnd(),
                "clicks", clicks,
                "impressions", impressions,
                "ctr", impressions == 0 ? 0 : clicks / impressions,
                "averagePosition", impressions == 0 ? 0 : weightedPosition / impressions);
    }

    private static Map<String, Object> dimensionTotals(SearchConsoleWorkbookParser.ParsedWorkbook workbook) {
        Map<String, Object> totals = new LinkedHashMap<>();
        totals.put("warning", "Each total belongs only to its exported dimension and is not a site total.");
        totals.put("queries", dimensionTotal(workbook.queries()));
        totals.put("pages", dimensionTotal(workbook.pages()));
        totals.put("countries", dimensionTotal(workbook.countries()));
        totals.put("devices", dimensionTotal(workbook.devices()));
        return totals;
    }

    private static Map<String, Object> dimensionTotal(List<SearchConsoleWorkbookParser.MetricRow> rows) {
        return Map.of(
                "rowCount", rows.size(),
                "clicks", rows.stream().mapToDouble(SearchConsoleWorkbookParser.MetricRow::clicks).sum(),
                "impressions", rows.stream().mapToDouble(SearchConsoleWorkbookParser.MetricRow::impressions).sum());
    }

    private static List<Map<String, Object>> languageBreakdown(List<AnalyzedRow> rows) {
        Map<String, double[]> values = new LinkedHashMap<>();
        rows.forEach(row -> {
            double[] totals = values.computeIfAbsent(row.language(), ignored -> new double[3]);
            totals[0] += row.metric().clicks();
            totals[1] += row.metric().impressions();
            totals[2] += 1;
        });
        return values.entrySet().stream().map(entry -> Map.<String, Object>of(
                "language", entry.getKey(),
                "clicks", entry.getValue()[0],
                "impressions", entry.getValue()[1],
                "rowCount", (int) entry.getValue()[2],
                "ctr", entry.getValue()[1] == 0 ? 0 : entry.getValue()[0] / entry.getValue()[1]))
                .sorted(Comparator.comparingDouble(row -> -((Number) row.get("impressions")).doubleValue()))
                .toList();
    }

    private static List<Map<String, Object>> metricMaps(List<SearchConsoleWorkbookParser.MetricRow> rows) {
        return rows.stream().map(row -> Map.<String, Object>of(
                "dimension", row.dimension(),
                "clicks", row.clicks(),
                "impressions", row.impressions(),
                "ctr", row.ctr(),
                "averagePosition", row.position())).toList();
    }

    private List<DraftBrief> draftBriefs(SearchConsoleWorkbookParser.ParsedWorkbook workbook) {
        List<DraftBrief> result = new ArrayList<>();
        addBrief(result, workbook, "FR-LANDING-CTR", "FR", "Improve the French RijVia landing-page snippet match",
                "Align the French title, description and visible promise with the high-impression landing-page intent.",
                row -> row.dimension().equalsIgnoreCase("https://readyroad.be/fr"), "TRAFFIC_RULES", "ICP-FR-THEORY");
        addBrief(result, workbook, "FR-FAQ-CTR", "FR", "Refine the French Belgian theory FAQ from real search demand",
                "Improve FAQ snippet relevance and same-language supporting links without inventing legal claims.",
                row -> row.dimension().contains("/fr/faq"), "PREPARATION_TIPS", "ICP-FR-THEORY");
        addBrief(result, workbook, "FR-C46", "FR", "French learning support for traffic sign C46",
                "Connect the sign explanation to relevant lessons and practice using the exported page evidence.",
                row -> row.dimension().contains("/fr/traffic-signs/C46"), "TRAFFIC_SIGNS", "ICP-FR-THEORY");
        addBrief(result, workbook, "FR-C3", "FR", "French learning support for traffic sign C3",
                "Clarify the sign meaning and create natural supporting links for French learners.",
                row -> row.dimension().contains("/fr/traffic-signs/C3"), "TRAFFIC_SIGNS", "ICP-FR-THEORY");
        addBrief(result, workbook, "FR-ZONE-F103", "FR", "French learning support for Zone-F103",
                "Improve the page's educational context and links while preserving verified traffic-sign facts.",
                row -> row.dimension().contains("/fr/traffic-signs/Zone-F103"), "TRAFFIC_SIGNS", "ICP-FR-THEORY");
        addBrief(result, workbook, "NL-AUTOWEG", "NL", "Wat is een autoweg in België?",
                "Explain the Belgian autoweg concept in natural Dutch and connect it to signs, lessons and practice.",
                row -> normalize(row.dimension()).contains("autoweg"), "TRAFFIC_RULES", "ICP-NL-PRACTICE");
        addBrief(result, workbook, "NL-AUTOWEG-AUTOSNELWEG", "NL", "Verschil tussen autoweg en autosnelweg in België",
                "Answer the comparison intent using only verified Belgian learning sources.",
                row -> normalize(row.dimension()).contains("autoweg")
                        && normalize(row.dimension()).contains("autosnelweg"), "TRAFFIC_RULES", "ICP-NL-PRACTICE");
        addBrief(result, workbook, "NL-UITHOLLING", "NL", "Overdwarse uitholling of ezelsrug: betekenis en verschil",
                "Resolve the observed terminology intent with learner-friendly Dutch examples.",
                row -> normalize(row.dimension()).contains("uitholling")
                        || normalize(row.dimension()).contains("ezelsrug"), "TRAFFIC_RULES", "ICP-NL-PRACTICE");
        addBrief(result, workbook, "NL-NOODSTOPSTROOK", "NL", "Noodstopstrook: betekenis en regels",
                "Explain the term and link to the relevant sign and lesson routes.",
                row -> normalize(row.dimension()).contains("noodstopstrook"), "TRAFFIC_RULES", "ICP-NL-PRACTICE");
        addBrief(result, workbook, "NL-VERBODSBORDEN", "NL", "Verbodsborden in België: overzicht en betekenis",
                "Support the observed traffic-sign cluster without keyword stuffing.",
                row -> normalize(row.dimension()).contains("verbodsbord"), "TRAFFIC_SIGNS", "ICP-NL-PRACTICE");
        addBrief(result, workbook, "AR-GRAVEL", "AR", "شرح علامة تطاير الحصى في بلجيكا",
                "شرح تعليمي طبيعي مبني على عبارة البحث والصفحة الفعلية، مع روابط للدروس والتدريب.",
                row -> row.dimension().contains("تطاير الحصى") || row.dimension().contains("/ar/traffic-signs/A17"),
                "TRAFFIC_SIGNS", "ICP-AR-BEGINNER");
        addBrief(result, workbook, "AR-PEDESTRIAN-ROAD", "AR", "جزء من الطريق مخصص للمشاة: المعنى في امتحان السياقة",
                "توضيح المعنى للمتعلم وربطه بصفحة العلامة والتدريب من دون ادعاءات قانونية غير موثقة.",
                row -> row.dimension().contains("مخصص للمشاة") || row.dimension().contains("/ar/traffic-signs/D9a"),
                "TRAFFIC_SIGNS", "ICP-AR-BEGINNER");
        return result;
    }

    private static void addBrief(
            List<DraftBrief> target,
            SearchConsoleWorkbookParser.ParsedWorkbook workbook,
            String key,
            String language,
            String title,
            String purpose,
            Predicate<SearchConsoleWorkbookParser.MetricRow> evidenceFilter,
            String pillar,
            String icp) {
        List<SearchConsoleWorkbookParser.MetricRow> evidence = new ArrayList<>();
        workbook.queries().stream().filter(evidenceFilter).limit(10).forEach(evidence::add);
        workbook.pages().stream().filter(evidenceFilter).limit(10).forEach(evidence::add);
        if (!evidence.isEmpty()) {
            List<String> queries = evidence.stream()
                    .map(SearchConsoleWorkbookParser.MetricRow::dimension)
                    .filter(value -> !value.startsWith("http"))
                    .distinct().toList();
            List<String> pages = evidence.stream()
                    .map(SearchConsoleWorkbookParser.MetricRow::dimension)
                    .filter(value -> value.startsWith("http"))
                    .distinct().toList();
            target.add(new DraftBrief(
                    key, language, title, purpose, queries, pages, pillar, icp,
                    Map.of("rows", metricMaps(evidence), "source", workbook.sourceFileName())));
        }
    }

    private static List<Map<String, Object>> internalLinks(List<SearchConsoleWorkbookParser.MetricRow> pages) {
        Set<String> paths = new LinkedHashSet<>();
        pages.forEach(row -> paths.add(path(row.dimension())));
        List<Map<String, Object>> links = new ArrayList<>();
        addLink(links, paths, "/fr", "/fr/faq", "questions fréquentes sur l'examen théorique");
        addLink(links, paths, "/fr", "/fr/traffic-signs/C46", "panneau C46");
        addLink(links, paths, "/fr/faq", "/fr/traffic-signs/C3", "panneau C3");
        addLink(links, paths, "/nl/traffic-signs/A13", "/nl/lessons", "regels voor deze verkeerssituatie");
        addLink(links, paths, "/nl/traffic-signs/A41", "/nl/practice", "oefen deze verkeersborden");
        addLink(links, paths, "/nl/traffic-signs/F95", "/nl/lessons", "meer over de autoweg");
        addLink(links, paths, "/ar/traffic-signs/A17", "/ar/practice", "تدرّب على علامات الخطر");
        addLink(links, paths, "/ar/traffic-signs/D9a", "/ar/lessons", "اقرأ القاعدة المرتبطة بالعلامة");
        return links;
    }

    private static void addLink(
            List<Map<String, Object>> links, Set<String> observedPaths, String source, String target, String anchor) {
        if (observedPaths.contains(source) && routeExists(target)) {
            links.add(Map.of(
                    "source", source,
                    "target", target,
                    "anchor", anchor,
                    "sameLanguage", locale(source).equals(locale(target)),
                    "status", "RECOMMENDED_NOT_APPLIED"));
        }
    }

    private static Map<String, Object> migrationMap(String oldUrl, String candidateDomain) {
        String path = path(oldUrl);
        String candidate = candidateDomain.replaceAll("/+$", "") + (path.equals("/") ? "/" : path);
        boolean exists = routeExists(path);
        Map<String, String> expectedHreflang = hreflang(path, candidateDomain);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("oldUrl", oldUrl);
        result.put("newUrlCandidate", candidate);
        result.put("routeExists", exists);
        result.put("locale", locale(path));
        result.put("pageType", pageType(path));
        result.put("canonicalCurrent", oldUrl);
        result.put("canonicalExpected", candidate);
        result.put("hreflangCurrent", "NOT_AVAILABLE_IN_SEARCH_CONSOLE_EXPORT");
        result.put("hreflangExpected", expectedHreflang);
        result.put("redirectRequiredLater", true);
        result.put("risk", !exists ? "BLOCKED_ROUTE_MAPPING"
                : oldUrl.startsWith("http://") ? "OLD_HTTP_URL" : "DOMAIN_MIGRATION");
        result.put("ownerDecisionRequired", false);
        result.put("releaseAuthorization", "OWNER_CONFIRMED_PENDING_GATES");
        return result;
    }

    private static Map<String, Object> technicalSeo(List<Map<String, Object>> urlMap) {
        long http = urlMap.stream().filter(row -> String.valueOf(row.get("oldUrl")).startsWith("http://")).count();
        long blocked = urlMap.stream().filter(row -> !Boolean.TRUE.equals(row.get("routeExists"))).count();
        return Map.of(
                "mappedUrls", urlMap.size(),
                "oldHttpUrls", http,
                "blockedRouteMappings", blocked,
                "canonicalConfigReady", true,
                "hreflangConfigReady", true,
                "sitemapConfigReady", true,
                "robotsConfigReady", true,
                "jsonLdReview", "STRUCTURAL_LOCAL_VALIDATION_REQUIRED",
                "liveRedirectsActivated", false,
                "changeOfAddressSubmitted", false);
    }

    static boolean routeExists(String value) {
        String localPath = stripLocale(path(value));
        return STATIC_ROUTES.contains(localPath)
                || DYNAMIC_LESSON.matcher(localPath).matches()
                || DYNAMIC_SIGN.matcher(localPath).matches();
    }

    private static String pageType(String value) {
        String localPath = stripLocale(path(value));
        if (localPath.equals("/")) return "LANDING";
        if (localPath.startsWith("/traffic-signs/")) return "TRAFFIC_SIGN_DETAIL";
        if (localPath.equals("/traffic-signs")) return "TRAFFIC_SIGN_INDEX";
        if (localPath.startsWith("/lessons/")) return "LESSON_DETAIL";
        if (localPath.equals("/lessons")) return "LESSON_INDEX";
        if (localPath.equals("/faq")) return "FAQ";
        return "PUBLIC_PAGE";
    }

    private static Map<String, String> hreflang(String value, String candidateDomain) {
        String basePath = stripLocale(path(value));
        String domain = candidateDomain.replaceAll("/+$", "");
        Map<String, String> values = new LinkedHashMap<>();
        values.put("en", domain + (basePath.equals("/") ? "/" : basePath));
        values.put("nl-BE", domain + "/nl" + (basePath.equals("/") ? "" : basePath));
        values.put("fr-BE", domain + "/fr" + (basePath.equals("/") ? "" : basePath));
        values.put("ar", domain + "/ar" + (basePath.equals("/") ? "" : basePath));
        values.put("x-default", values.get("en"));
        return Map.copyOf(values);
    }

    private static String queryLanguage(String value) {
        if (ARABIC.matcher(value).find()) return "AR";
        if (containsAny(value, "panneau", "signalisation", "permis", "belgique", "théorique", "code de la route")) return "FR";
        if (containsAny(value, "autoweg", "autosnelweg", "verkeers", "voorrang", "rijstrook", "noodstopstrook", "belgië", "theorie")) return "NL";
        if (containsAny(value, "traffic", "driving", "theory", "belgian", "road sign")) return "EN";
        return "UNKNOWN";
    }

    private static String locale(String value) {
        String path = path(value);
        if (path.equals("/ar") || path.startsWith("/ar/")) return "AR";
        if (path.equals("/nl") || path.startsWith("/nl/")) return "NL";
        if (path.equals("/fr") || path.startsWith("/fr/")) return "FR";
        return "EN";
    }

    private static String stripLocale(String value) {
        String path = path(value);
        for (String prefix : List.of("/ar", "/nl", "/fr")) {
            if (path.equals(prefix)) return "/";
            if (path.startsWith(prefix + "/")) return path.substring(prefix.length());
        }
        return path;
    }

    private static boolean isReadyRoadUrl(String value) {
        try {
            String host = URI.create(value).getHost();
            return host != null && (host.equalsIgnoreCase("readyroad.be") || host.equalsIgnoreCase("www.readyroad.be"));
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private static String path(String value) {
        if (value == null || value.isBlank()) return "/";
        try {
            URI uri = new URI(value);
            String path = uri.getPath();
            return path == null || path.isBlank() ? "/" : normalizePath(path);
        } catch (URISyntaxException error) {
            int query = value.indexOf('?');
            return normalizePath(query >= 0 ? value.substring(0, query) : value);
        }
    }

    private static String normalizePath(String value) {
        String result = value.startsWith("/") ? value : "/" + value;
        return result.length() > 1 ? result.replaceAll("/+$", "") : result;
    }

    private static boolean containsAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token)) return true;
        }
        return false;
    }

    private static boolean between(double value, double minimum, double maximum) {
        return value >= minimum && value <= maximum;
    }

    private static int priorityOrder(String priority) {
        return switch (priority) {
            case "P0" -> 0;
            case "P1" -> 1;
            case "P2" -> 2;
            default -> 3;
        };
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    public record AnalyzedRow(
            SearchConsoleWorkbookParser.MetricRow metric,
            String language,
            String brandClassification,
            List<String> classifications,
            String state,
            String priority,
            String recommendedActionCategory,
            String confidenceLevel,
            int score,
            boolean relevant) {

        Map<String, Object> asReport(String dimensionType) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("dimensionType", dimensionType);
            result.put("dimension", metric.dimension());
            result.put("language", language);
            result.put("brandClassification", brandClassification);
            result.put("classifications", classifications);
            result.put("state", state);
            result.put("priority", priority);
            result.put("recommendedActionCategory", recommendedActionCategory);
            result.put("confidenceLevel", confidenceLevel);
            result.put("score", score);
            result.put("clicks", metric.clicks());
            result.put("impressions", metric.impressions());
            result.put("ctr", metric.ctr());
            result.put("averagePosition", metric.position());
            return result;
        }
    }

    public record DraftBrief(
            String key,
            String language,
            String workingTitle,
            String purpose,
            List<String> targetQueries,
            List<String> supportingPages,
            String contentPillarKey,
            String icpKey,
            Map<String, Object> evidence) {

        Map<String, Object> asReport() {
            return Map.of(
                    "key", key,
                    "language", language,
                    "workingTitle", workingTitle,
                    "status", "WAITING_OWNER_REVIEW",
                    "contentPillarKey", contentPillarKey,
                    "icpKey", icpKey,
                    "conversionGoal", "OWNER_DECISION_REQUIRED");
        }
    }

    public record Analysis(
            List<AnalyzedRow> queryRows,
            List<AnalyzedRow> pageRows,
            List<DraftBrief> draftBriefs,
            Map<String, Object> report) {}
}
