package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.util.RouteCodeNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class CanonicalSignCatalogService {

    private static final Logger log = LoggerFactory.getLogger(CanonicalSignCatalogService.class);
    private static final int EXPECTED_CANONICAL_SIGN_COUNT = 184;

    private static final Map<String, SignCategory> CATEGORY_NAME_TO_ENUM = Map.ofEntries(
            Map.entry("gevaarsborden", SignCategory.DANGER),
            Map.entry("voorrangsborden", SignCategory.PRIORITY),
            Map.entry("verbodsborden", SignCategory.PROHIBITION),
            Map.entry("gebodsborden", SignCategory.MANDATORY),
            Map.entry("parkeer- en stilstaanborden", SignCategory.PARKING),
            Map.entry("parkeerborden", SignCategory.PARKING),
            Map.entry("parkeren", SignCategory.PARKING),
            Map.entry("aanwijzingsborden", SignCategory.INFORMATION),
            Map.entry("onderborden", SignCategory.ADDITIONAL),
            Map.entry("zoneborden", SignCategory.ZONE),
            Map.entry("afbakeningsborden", SignCategory.CYCLIST),
            Map.entry("informatieborden_en_tijdelijke_verkeersmaatregelen", SignCategory.INFORMATION),
            Map.entry("informatieborden en tijdelijke verkeersmaatregelen", SignCategory.INFORMATION));

    private final ObjectMapper objectMapper;
    private final ResourcePatternResolver resourcePatternResolver;

    @Value("${readyroad.signs-import.path:src/main/resources/data/signs_import}")
    private String signsImportPath;

    private volatile List<CanonicalSignSeed> seeds = List.of();
    private volatile Map<String, CanonicalSignSeed> seedsByRoute = Map.of();
    private volatile Map<String, CanonicalSignSeed> seedsByCode = Map.of();
    private volatile Map<String, CanonicalSignSeed> seedsByImage = Map.of();
    private volatile Map<String, Integer> variantCountByCode = Map.of();
    private volatile Set<String> allowedImagePaths = Set.of();

    public CanonicalSignCatalogService(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
    }

    @PostConstruct
    public void refresh() {
        List<CanonicalSignSeed> loadedSeeds = loadCanonicalSeeds();
        if (loadedSeeds.size() != EXPECTED_CANONICAL_SIGN_COUNT) {
            throw new IllegalStateException(
                    "Canonical signs_import must contain exactly " + EXPECTED_CANONICAL_SIGN_COUNT
                            + " valid signs, found " + loadedSeeds.size());
        }
        Map<String, CanonicalSignSeed> byRoute = new LinkedHashMap<>();
        Map<String, CanonicalSignSeed> byCode = new LinkedHashMap<>();
        Map<String, CanonicalSignSeed> byImage = new LinkedHashMap<>();
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (CanonicalSignSeed seed : loadedSeeds) {
            if (byRoute.containsKey(seed.routeKey())) {
                throw new IllegalStateException("Duplicate canonical route key detected: " + seed.routeCode());
            }
            byRoute.put(seed.routeKey(), seed);
            byCode.putIfAbsent(seed.normalizedCode(), seed);
            byImage.putIfAbsent(seed.imagePath(), seed);
            counts.merge(seed.normalizedCode(), 1, (left, right) -> left + right);
        }

        this.seeds = List.copyOf(loadedSeeds);
        this.seedsByRoute = Map.copyOf(byRoute);
        this.seedsByCode = Map.copyOf(byCode);
        this.seedsByImage = Map.copyOf(byImage);
        this.variantCountByCode = Map.copyOf(counts);
        this.allowedImagePaths = Set.copyOf(byImage.keySet());

        log.info(
                "Loaded canonical sign catalog: {} seeds, {} route keys, {} allowed images",
                seeds.size(),
                seedsByRoute.size(),
                allowedImagePaths.size());
    }

    public List<CanonicalSignSeed> getCanonicalSeeds() {
        return seeds;
    }

    public Optional<CanonicalSignSeed> findSeedByRouteCode(String routeCode) {
        if (isBlank(routeCode)) {
            return Optional.empty();
        }

        String normalizedRoute = normalizeRouteKey(routeCode);
        CanonicalSignSeed exact = seedsByRoute.get(normalizedRoute);
        if (exact != null) {
            return Optional.of(exact);
        }

        return Optional.ofNullable(seedsByCode.get(normalizeCode(routeCode)));
    }

    public Optional<CanonicalSignSeed> findSeedByImagePath(String imagePath) {
        String normalized = normalizeImagePath(imagePath);
        if (normalized.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(seedsByImage.get(normalized));
    }

    public boolean hasMultipleVariants(String signCode) {
        return variantCountByCode.getOrDefault(normalizeCode(signCode), 0) > 1;
    }

    public String routeCodeFor(RoadSign sign) {
        if (sign == null) {
            return "";
        }

        Optional<CanonicalSignSeed> seedOpt = findSeedFor(sign);
        if (seedOpt.isPresent()) {
            return seedOpt.get().routeCode();
        }

        String routeKey = normalizeRouteKey(sign.getNormalizedSignCode());
        return !isBlank(sign.getSignCode()) ? sign.getSignCode() : routeKey;
    }

    public ResolvedSignData resolve(RoadSign sign) {
        String imagePath = firstUsable(sign.getImagePath());
        String normalizedImagePath = normalizeImagePath(imagePath);
        boolean allowed = !normalizedImagePath.isBlank() && allowedImagePaths.contains(normalizedImagePath);
        String nameEn = firstUsable(sign.getNameEn());
        String nameAr = firstUsable(sign.getNameAr());
        String nameNl = firstUsable(sign.getNameNl());
        String nameFr = firstUsable(sign.getNameFr());

        String descriptionEn = firstUsable(sign.getDescriptionEn());
        String descriptionAr = firstUsable(sign.getDescriptionAr());
        String descriptionNl = firstUsable(sign.getDescriptionNl());
        String descriptionFr = firstUsable(sign.getDescriptionFr());

        String summaryEn = firstUsable(sign.getSummaryEn());
        String summaryAr = firstUsable(sign.getSummaryAr());
        String summaryNl = firstUsable(sign.getSummaryNl());
        String summaryFr = firstUsable(sign.getSummaryFr());
        String driverGuidanceEn = firstUsable(sign.getDriverGuidanceEn());
        String driverGuidanceAr = firstUsable(sign.getDriverGuidanceAr());
        String driverGuidanceNl = firstUsable(sign.getDriverGuidanceNl());
        String driverGuidanceFr = firstUsable(sign.getDriverGuidanceFr());

        return new ResolvedSignData(
                firstUsable(sign.getSignCode()),
                routeCodeFor(sign),
                nameEn,
                nameAr,
                nameNl,
                nameFr,
                descriptionEn,
                descriptionAr,
                descriptionNl,
                descriptionFr,
                summaryEn,
                summaryAr,
                summaryNl,
                summaryFr,
                driverGuidanceEn,
                driverGuidanceAr,
                driverGuidanceNl,
                driverGuidanceFr,
                safeList(sign.getExceptionsEn()),
                safeList(sign.getExceptionsAr()),
                safeList(sign.getExceptionsNl()),
                safeList(sign.getExceptionsFr()),
                normalizedImagePath,
                allowed);
    }

    public boolean isPubliclyAllowed(RoadSign sign) {
        return resolve(sign).publiclyAllowed();
    }

    public void applyCanonicalFields(RoadSign sign) {
        Optional<CanonicalSignSeed> seedOpt = findSeedFor(sign);
        if (seedOpt.isEmpty()) {
            return;
        }

        applyCanonicalFields(sign, seedOpt.get());
    }

    public void applyCanonicalFields(RoadSign sign, CanonicalSignSeed seed) {
        sign.setSignCode(seed.routeCode());
        sign.setNormalizedSignCode(seed.routeKey());
        sign.setCategory(seed.category());
        sign.setNameEn(seed.nameEn());
        sign.setNameAr(seed.nameAr());
        sign.setNameNl(seed.nameNl());
        sign.setNameFr(seed.nameFr());
        sign.setDescriptionEn(seed.descriptionEn());
        sign.setDescriptionAr(seed.descriptionAr());
        sign.setDescriptionNl(seed.descriptionNl());
        sign.setDescriptionFr(seed.descriptionFr());
        sign.setSummaryEn(seed.summaryEn());
        sign.setSummaryAr(seed.summaryAr());
        sign.setSummaryNl(seed.summaryNl());
        sign.setSummaryFr(seed.summaryFr());
        sign.setDriverGuidanceEn(seed.driverGuidanceEn());
        sign.setDriverGuidanceAr(seed.driverGuidanceAr());
        sign.setDriverGuidanceNl(seed.driverGuidanceNl());
        sign.setDriverGuidanceFr(seed.driverGuidanceFr());
        sign.setExceptionsEn(seed.exceptionsEn());
        sign.setExceptionsAr(seed.exceptionsAr());
        sign.setExceptionsNl(seed.exceptionsNl());
        sign.setExceptionsFr(seed.exceptionsFr());
        sign.setSeriousViolation(seed.seriousViolation());
        if (!seed.imagePath().isBlank()) {
            sign.setImagePath(seed.imagePath());
        }
    }

    public Optional<CanonicalSignSeed> findSeedFor(RoadSign sign) {
        if (sign == null) {
            return Optional.empty();
        }

        if (!isBlank(sign.getNormalizedSignCode())) {
            CanonicalSignSeed byRoute = seedsByRoute.get(normalizeRouteKey(sign.getNormalizedSignCode()));
            if (byRoute != null) {
                return Optional.of(byRoute);
            }
        }

        if (!isBlank(sign.getSignCode())) {
            CanonicalSignSeed byCode = seedsByCode.get(normalizeCode(sign.getSignCode()));
            if (byCode != null) {
                return Optional.of(byCode);
            }
        }

        if (!isBlank(sign.getImagePath())) {
            CanonicalSignSeed byImage = seedsByImage.get(normalizeImagePath(sign.getImagePath()));
            if (byImage != null) {
                return Optional.of(byImage);
            }
        }

        return Optional.empty();
    }

    private List<CanonicalSignSeed> loadCanonicalSeeds() {
        return loadImportSignSeeds();
    }

    private List<CanonicalSignSeed> loadImportSignSeeds() {
        try {
            List<Resource> signResources = resolveImportSignResources();
            Map<String, CanonicalSignSeed> loaded = new LinkedHashMap<>();

            for (Resource resource : signResources) {
                try (InputStream input = resource.getInputStream()) {
                    JsonNode node = objectMapper.readTree(input);
                    CanonicalSignSeed seed = importSeed(node);
                    if (seed != null) {
                        CanonicalSignSeed duplicate = loaded.putIfAbsent(seed.routeKey(), seed);
                        if (duplicate != null) {
                            throw new IllegalStateException(
                                    "Duplicate canonical route key detected while loading: " + seed.routeCode());
                        }
                    }
                }
            }

            if (!loaded.isEmpty()) {
                log.info("Loaded {} canonical road signs from signs_import", loaded.size());
            }
            return new ArrayList<>(loaded.values());
        } catch (IOException ex) {
            log.error("Failed to load canonical signs from signs_import {}: {}", signsImportPath, ex.getMessage(), ex);
            return List.of();
        }
    }

    private List<Resource> resolveImportSignResources() throws IOException {
        Map<String, Resource> resourcesByDescription = new LinkedHashMap<>();
        File importDirectory = new File(signsImportPath);
        if (!importDirectory.isAbsolute()) {
            importDirectory = new File(System.getProperty("user.dir"), signsImportPath);
        }
        if (importDirectory.exists() && importDirectory.isDirectory()) {
            try (Stream<Path> paths = Files.walk(importDirectory.toPath(), 2)) {
                paths.filter(path -> "sign.json".equals(path.getFileName().toString()))
                        .sorted(Comparator.comparing(Path::toString))
                        .forEach(path -> resourcesByDescription.put(
                                path.toAbsolutePath().normalize().toString(),
                                new FileSystemResource(path)));
            }
            List<Resource> resources = new ArrayList<>(resourcesByDescription.values());
            resources.sort(Comparator.comparing(Resource::getDescription));
            return resources;
        }

        for (Resource resource : resourcePatternResolver.getResources("classpath*:data/signs_import/*/sign.json")) {
            if (resource.exists()) {
                resourcesByDescription.putIfAbsent(resource.getDescription(), resource);
            }
        }

        List<Resource> resources = new ArrayList<>(resourcesByDescription.values());
        resources.sort(Comparator.comparing(Resource::getDescription));
        return resources;
    }

    private CanonicalSignSeed importSeed(JsonNode node) {
        String routeCode = firstUsable(text(node, "route_code"), text(node, "id"), text(node, "code"));
        String signCode = firstUsable(text(node, "code"), routeCode);
        String routeKey = normalizeRouteKey(routeCode);
        String normalizedCode = normalizeCode(signCode);
        SignCategory category = mapCategory(text(node, "category"));
        String imagePath = normalizeImagePath(firstUsable(text(node, "image_path"), text(node, "imagePath"),
                text(node, "image")));

        if (routeKey.isBlank() || normalizedCode.isBlank() || category == null || imagePath.isBlank()) {
            return null;
        }

        JsonNode i18n = node.path("i18n");
        String nameEn = requiredImportText(i18n, "EN", "name", routeCode);
        String nameAr = requiredImportText(i18n, "AR", "name", routeCode);
        String nameNl = requiredImportText(i18n, "NL", "name", routeCode);
        String nameFr = requiredImportText(i18n, "FR", "name", routeCode);
        String descriptionEn = requiredImportText(i18n, "EN", "description", routeCode);
        String descriptionAr = requiredImportText(i18n, "AR", "description", routeCode);
        String descriptionNl = requiredImportText(i18n, "NL", "description", routeCode);
        String descriptionFr = requiredImportText(i18n, "FR", "description", routeCode);
        String summaryEn = requiredImportText(i18n, "EN", "summary", routeCode);
        String summaryAr = requiredImportText(i18n, "AR", "summary", routeCode);
        String summaryNl = requiredImportText(i18n, "NL", "summary", routeCode);
        String summaryFr = requiredImportText(i18n, "FR", "summary", routeCode);
        String driverGuidanceEn = requiredImportText(i18n, "EN", "driver_guidance", routeCode);
        String driverGuidanceAr = requiredImportText(i18n, "AR", "driver_guidance", routeCode);
        String driverGuidanceNl = requiredImportText(i18n, "NL", "driver_guidance", routeCode);
        String driverGuidanceFr = requiredImportText(i18n, "FR", "driver_guidance", routeCode);
        List<String> exceptionsEn = requiredImportTextList(i18n, "EN", "exceptions", routeCode);
        List<String> exceptionsAr = requiredImportTextList(i18n, "AR", "exceptions", routeCode);
        List<String> exceptionsNl = requiredImportTextList(i18n, "NL", "exceptions", routeCode);
        List<String> exceptionsFr = requiredImportTextList(i18n, "FR", "exceptions", routeCode);
        boolean seriousViolation = node.path("serious_violation").asBoolean(false);

        return new CanonicalSignSeed(
                routeCode,
                routeKey,
                signCode,
                normalizedCode,
                category,
                nameEn,
                nameAr,
                nameNl,
                nameFr,
                descriptionEn,
                descriptionAr,
                descriptionNl,
                descriptionFr,
                summaryEn,
                summaryAr,
                summaryNl,
                summaryFr,
                driverGuidanceEn,
                driverGuidanceAr,
                driverGuidanceNl,
                driverGuidanceFr,
                exceptionsEn,
                exceptionsAr,
                exceptionsNl,
                exceptionsFr,
                seriousViolation,
                imagePath);
    }

    private String requiredImportText(JsonNode i18n, String language, String field, String routeCode) {
        String value = importText(i18n, language, field);
        if (isBlank(value)) {
            throw new IllegalStateException(
                    "Missing i18n." + language + "." + field + " in signs_import/" + routeCode + "/sign.json");
        }
        return value;
    }

    private List<String> requiredImportTextList(JsonNode i18n, String language, String field, String routeCode) {
        JsonNode values = i18n.path(language).path(field);
        if (!values.isArray()) {
            throw new IllegalStateException(
                    "Missing i18n." + language + "." + field + " array in signs_import/" + routeCode + "/sign.json");
        }

        List<String> result = new ArrayList<>();
        for (JsonNode value : values) {
            if (!value.isTextual() || isBlank(value.asText())) {
                throw new IllegalStateException(
                        "Invalid i18n." + language + "." + field + " entry in signs_import/" + routeCode
                                + "/sign.json");
            }
            result.add(value.asText().trim());
        }
        return List.copyOf(result);
    }

    private List<String> safeList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return List.copyOf(values);
    }

    private static SignCategory mapCategory(String value) {
        if (isBlank(value)) {
            return null;
        }
        SignCategory mapped = CATEGORY_NAME_TO_ENUM.get(value.trim().toLowerCase(Locale.ROOT));
        if (mapped != null) {
            return mapped;
        }
        try {
            return SignCategory.valueOf(value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_'));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String normalizeCode(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeRouteKey(String value) {
        return RouteCodeNormalizer.normalize(value);
    }

    private static String normalizeImagePath(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim().replace('\\', '/');
        if (normalized.isBlank()) {
            return "";
        }
        if (normalized.startsWith("./")) {
            normalized = normalized.substring(1);
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        return normalized;
    }

    private static String text(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        String value = node.get(field).asText();
        return value == null ? null : value.trim();
    }

    private static String importText(JsonNode i18n, String lang, String field) {
        if (i18n == null || !i18n.has(lang)) {
            return null;
        }
        return text(i18n.path(lang), field);
    }

    private static String firstUsable(String... candidates) {
        for (String candidate : candidates) {
            if (!isBroken(candidate)) {
                return candidate.trim();
            }
        }
        return "";
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isBroken(String value) {
        if (isBlank(value)) {
            return true;
        }
        return value.contains("????")
                || value.contains("Ã")
                || value.contains("Ù")
                || value.contains("Ø")
                || value.contains("�");
    }

    public record CanonicalSignSeed(
            String routeCode,
            String routeKey,
            String signCode,
            String normalizedCode,
            SignCategory category,
            String nameEn,
            String nameAr,
            String nameNl,
            String nameFr,
            String descriptionEn,
            String descriptionAr,
            String descriptionNl,
            String descriptionFr,
            String summaryEn,
            String summaryAr,
            String summaryNl,
            String summaryFr,
            String driverGuidanceEn,
            String driverGuidanceAr,
            String driverGuidanceNl,
            String driverGuidanceFr,
            List<String> exceptionsEn,
            List<String> exceptionsAr,
            List<String> exceptionsNl,
            List<String> exceptionsFr,
            boolean seriousViolation,
            String imagePath) {
    }

    public record ResolvedSignData(
            String signCode,
            String routeCode,
            String nameEn,
            String nameAr,
            String nameNl,
            String nameFr,
            String descriptionEn,
            String descriptionAr,
            String descriptionNl,
            String descriptionFr,
            String summaryEn,
            String summaryAr,
            String summaryNl,
            String summaryFr,
            String driverGuidanceEn,
            String driverGuidanceAr,
            String driverGuidanceNl,
            String driverGuidanceFr,
            List<String> exceptionsEn,
            List<String> exceptionsAr,
            List<String> exceptionsNl,
            List<String> exceptionsFr,
            String imagePath,
            boolean publiclyAllowed) {
    }
}
