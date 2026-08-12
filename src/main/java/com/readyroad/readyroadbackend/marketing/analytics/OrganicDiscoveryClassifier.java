package com.readyroad.readyroadbackend.marketing.analytics;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyReadService;
import java.net.URI;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganicDiscoveryClassifier {

    private static final Pattern ARABIC = Pattern.compile("[\\p{InArabic}]");
    private static final Set<String> OWN_BRAND = Set.of("readyroad", "ready road", "readyroad.be");
    private static final Set<String> AMBIGUOUS_BRAND = Set.of("ready to road", "readytoroad");
    private static final Set<String> NAVIGATIONAL = Set.of("login", "inloggen", "connexion", "تسجيل", "دخول");
    private static final Set<String> TRANSACTIONAL = Set.of(
            "exam", "test", "practice", "register", "examen", "oefenen", "inschrijven",
            "exercice", "inscription", "امتحان", "اختبار", "تدريب", "تسجيل");
    private static final Set<String> INFORMATIONAL = Set.of(
            "what", "how", "why", "difference", "wat", "hoe", "waarom", "verschil",
            "quoi", "comment", "pourquoi", "différence", "ما", "كيف", "لماذا", "الفرق");

    private final MarketingStrategyReadService strategyReadService;

    public AnalyticsModels.BrandClassification brand(String query) {
        String normalized = normalize(query);
        if (OWN_BRAND.contains(normalized)) {
            return AnalyticsModels.BrandClassification.OWN_BRAND;
        }
        if (AMBIGUOUS_BRAND.contains(normalized)) {
            return AnalyticsModels.BrandClassification.COMPETITOR_OR_AMBIGUOUS_BRAND;
        }
        return AnalyticsModels.BrandClassification.NON_BRAND;
    }

    public boolean longTail(String query) {
        return words(query).length >= 5;
    }

    public AnalyticsModels.SearchIntent intent(String query) {
        String normalized = normalize(query);
        if (brand(query) != AnalyticsModels.BrandClassification.NON_BRAND
                || containsAny(normalized, NAVIGATIONAL)) {
            return AnalyticsModels.SearchIntent.NAVIGATIONAL;
        }
        if (containsAny(normalized, TRANSACTIONAL)) {
            return AnalyticsModels.SearchIntent.TRANSACTIONAL;
        }
        if (longTail(query) || containsAny(normalized, INFORMATIONAL)) {
            return AnalyticsModels.SearchIntent.INFORMATIONAL;
        }
        return AnalyticsModels.SearchIntent.UNKNOWN;
    }

    public String language(String page, String query) {
        String path = path(page);
        if (path.equals("/ar") || path.startsWith("/ar/")) {
            return "AR";
        }
        if (path.equals("/nl") || path.startsWith("/nl/")) {
            return "NL";
        }
        if (path.equals("/fr") || path.startsWith("/fr/")) {
            return "FR";
        }
        if (ARABIC.matcher(query == null ? "" : query).find()) {
            return "AR";
        }
        return page == null || page.isBlank() ? "UNKNOWN" : "EN";
    }

    public boolean relevant(String query) {
        Set<String> queryWords = meaningfulWords(query);
        if (queryWords.isEmpty()) {
            return false;
        }
        var strategy = strategyReadService.snapshot();
        Set<String> strategyWords = new HashSet<>();
        strategy.contentPillars().stream().filter(pillar -> pillar.active()).forEach(pillar -> {
            strategyWords.addAll(meaningfulWords(pillar.name()));
            strategyWords.addAll(meaningfulWords(pillar.pillarKey()));
        });
        strategy.icps().stream().filter(icp -> icp.active()).forEach(icp -> {
            strategyWords.addAll(meaningfulWords(icp.primaryGoal()));
            strategyWords.addAll(meaningfulWords(icp.mainProblem()));
            strategyWords.addAll(meaningfulWords(icp.searchIntent()));
        });
        strategy.usps().stream().filter(usp -> usp.active()).forEach(usp -> {
            strategyWords.addAll(meaningfulWords(usp.title()));
            strategyWords.addAll(meaningfulWords(usp.description()));
        });
        return queryWords.stream().anyMatch(strategyWords::contains);
    }

    private static boolean containsAny(String value, Set<String> tokens) {
        return tokens.stream().anyMatch(value::contains);
    }

    private static Set<String> meaningfulWords(String value) {
        Set<String> result = new HashSet<>();
        for (String word : words(value)) {
            if (word.length() >= 3) {
                result.add(word);
            }
        }
        return result;
    }

    private static String[] words(String value) {
        String normalized = normalize(value);
        if (normalized.isBlank()) {
            return new String[0];
        }
        return Arrays.stream(normalized.split("[^\\p{L}\\p{N}]+"))
                .filter(word -> !word.isBlank())
                .toArray(String[]::new);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }

    private static String path(String page) {
        if (page == null || page.isBlank()) {
            return "";
        }
        try {
            return URI.create(page).getPath();
        } catch (IllegalArgumentException ignored) {
            return page;
        }
    }
}
