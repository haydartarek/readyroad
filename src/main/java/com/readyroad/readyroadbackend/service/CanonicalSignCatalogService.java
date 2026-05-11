package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.util.DrivingTextSanitizer;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class CanonicalSignCatalogService {

    private static final Logger log = LoggerFactory.getLogger(CanonicalSignCatalogService.class);

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
    private final ResourceLoader resourceLoader;
    private final ResourcePatternResolver resourcePatternResolver;

    @Value("${readyroad.signs.canonical-path:data/signs.json}")
    private String canonicalSignsPath;

    @Value("${readyroad.signs-import.path:src/main/resources/data/signs_import}")
    private String signsImportPath;

    @Value("${readyroad.signs.images-manifest-path:data/canonical_sign_images.json}")
    private String canonicalImagesPath;

    private volatile List<CanonicalSignSeed> seeds = List.of();
    private volatile Map<String, CanonicalSignSeed> seedsByRoute = Map.of();
    private volatile Map<String, CanonicalSignSeed> seedsByCode = Map.of();
    private volatile Map<String, CanonicalSignSeed> seedsByImage = Map.of();
    private volatile Map<String, Integer> variantCountByCode = Map.of();
    private volatile Set<String> allowedImagePaths = Set.of();

    public CanonicalSignCatalogService(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
    }

    @PostConstruct
    public void refresh() {
        List<CanonicalSignSeed> loadedSeeds = loadCanonicalSeeds();
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
        this.allowedImagePaths = loadAllowedImagePaths();

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
        CanonicalSignSeed seed = findSeedFor(sign).orElse(null);

        String imagePath = firstUsable(seed != null ? seed.imagePath() : null, sign.getImagePath());
        String normalizedImagePath = normalizeImagePath(imagePath);
        boolean allowed = !normalizedImagePath.isBlank() && allowedImagePaths.contains(normalizedImagePath);
        String nameEn = firstUsable(seed != null ? seed.nameEn() : null, sign.getNameEn(), sign.getSignCode());
        String nameAr = firstUsable(seed != null ? seed.nameAr() : null, sign.getNameAr(), nameEn, sign.getSignCode());
        String nameNl = firstUsable(seed != null ? seed.nameNl() : null, sign.getNameNl(), nameEn, sign.getSignCode());
        String nameFr = firstUsable(seed != null ? seed.nameFr() : null, sign.getNameFr(), nameEn, sign.getSignCode());

        String descriptionEn = DrivingTextSanitizer.sanitize("EN",
                firstUsable(seed != null ? seed.shortDescriptionEn() : null, sign.getDescriptionEn()));
        String descriptionAr = DrivingTextSanitizer.sanitize("AR",
                firstUsable(seed != null ? seed.shortDescriptionAr() : null, sign.getDescriptionAr()));
        String descriptionNl = DrivingTextSanitizer.sanitize("NL",
                firstUsable(seed != null ? seed.shortDescriptionNl() : null, sign.getDescriptionNl()));
        String descriptionFr = DrivingTextSanitizer.sanitize("FR",
                firstUsable(seed != null ? seed.shortDescriptionFr() : null, sign.getDescriptionFr()));

        String longDescriptionEn = DrivingTextSanitizer.sanitize("EN",
                firstUsable(seed != null ? seed.longDescriptionEn() : null, descriptionEn));
        String longDescriptionNl = DrivingTextSanitizer.sanitize("NL",
                firstUsable(seed != null ? seed.longDescriptionNl() : null, descriptionNl));
        String longDescriptionFr = DrivingTextSanitizer.sanitize("FR",
                firstUsable(seed != null ? seed.longDescriptionFr() : null, descriptionFr));
        String longDescriptionAr = DrivingTextSanitizer.sanitize("AR",
                firstUsable(seed != null ? seed.longDescriptionAr() : null, descriptionAr));

        return new ResolvedSignData(
                firstUsable(seed != null ? seed.signCode() : null, sign.getSignCode()),
                routeCodeFor(sign),
                nameEn,
                nameAr,
                nameNl,
                nameFr,
                descriptionEn,
                descriptionAr,
                descriptionNl,
                descriptionFr,
                longDescriptionEn,
                longDescriptionNl,
                longDescriptionFr,
                longDescriptionAr,
                !isBlank(longDescriptionEn)
                        || !isBlank(longDescriptionNl)
                        || !isBlank(longDescriptionFr)
                        || !isBlank(longDescriptionAr),
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

        CanonicalSignSeed seed = seedOpt.get();
        sign.setSignCode(seed.routeCode());
        sign.setNormalizedSignCode(seed.routeKey());
        sign.setCategory(seed.category());
        sign.setNameEn(seed.nameEn());
        sign.setNameAr(seed.nameAr());
        sign.setNameNl(seed.nameNl());
        sign.setNameFr(seed.nameFr());
        sign.setDescriptionEn(DrivingTextSanitizer.sanitize("EN", seed.shortDescriptionEn()));
        sign.setDescriptionAr(DrivingTextSanitizer.sanitize("AR", seed.shortDescriptionAr()));
        sign.setDescriptionNl(DrivingTextSanitizer.sanitize("NL", seed.shortDescriptionNl()));
        sign.setDescriptionFr(DrivingTextSanitizer.sanitize("FR", seed.shortDescriptionFr()));
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
        Map<String, CanonicalSignSeed> legacySeeds = loadLegacyCanonicalSeedsByRoute();
        List<CanonicalSignSeed> importedSeeds = loadImportSignSeeds(legacySeeds);
        if (!importedSeeds.isEmpty()) {
            return importedSeeds;
        }
        return new ArrayList<>(legacySeeds.values());
    }

    private Map<String, CanonicalSignSeed> loadLegacyCanonicalSeedsByRoute() {
        try (InputStream input = openInputStream(canonicalSignsPath)) {
            List<JsonNode> entries = objectMapper.readValue(input, new TypeReference<List<JsonNode>>() {
            });
            Map<String, CanonicalSignSeed> loaded = new LinkedHashMap<>();

            for (JsonNode node : entries) {
                String routeCode = firstUsable(text(node, "id"), text(node, "code"));
                String signCode = firstUsable(text(node, "code"), routeCode);
                String routeKey = normalizeRouteKey(routeCode);
                String normalizedCode = normalizeCode(signCode);
                SignCategory category = mapCategory(text(node, "category"));
                String imagePath = normalizeImagePath(firstUsable(text(node, "image"), text(node, "imagePath")));

                if (routeKey.isBlank() || normalizedCode.isBlank() || category == null || imagePath.isBlank()) {
                    continue;
                }

                loaded.put(routeKey, new CanonicalSignSeed(
                        routeCode,
                        routeKey,
                        signCode,
                        normalizedCode,
                        category,
                        firstUsable(text(node, "title_en"), text(node, "title")),
                        firstUsable(text(node, "title_ar"), text(node, "title_en"), text(node, "title")),
                        firstUsable(text(node, "title_nl"), text(node, "title")),
                        firstUsable(text(node, "title_fr"), text(node, "title_en"), text(node, "title")),
                        firstUsable(text(node, "long_description_en_official"), text(node, "long_description_en")),
                        firstUsable(text(node, "long_description_ar_official"), text(node, "long_description_ar")),
                        firstUsable(text(node, "long_description_nl_official"), text(node, "long_description_nl"),
                                text(node, "long_description")),
                        firstUsable(text(node, "long_description_fr_official"), text(node, "long_description_fr")),
                        firstUsable(text(node, "long_description_en"), text(node, "long_description_en_official")),
                        firstUsable(text(node, "long_description_ar"), text(node, "long_description_ar_official")),
                        firstUsable(text(node, "long_description_nl"), text(node, "long_description_nl_official"),
                                text(node, "long_description")),
                        firstUsable(text(node, "long_description_fr"), text(node, "long_description_fr_official")),
                        imagePath));
            }

            return loaded;
        } catch (IOException ex) {
            log.error("Failed to load canonical signs catalog from {}: {}", canonicalSignsPath, ex.getMessage(), ex);
            return Map.of();
        }
    }

    private List<CanonicalSignSeed> loadImportSignSeeds(Map<String, CanonicalSignSeed> legacySeedsByRoute) {
        try {
            List<Resource> signResources = resolveImportSignResources();
            Map<String, CanonicalSignSeed> loaded = new LinkedHashMap<>();

            for (Resource resource : signResources) {
                try (InputStream input = resource.getInputStream()) {
                    JsonNode node = objectMapper.readTree(input);
                    CanonicalSignSeed seed = importSeed(node, legacySeedsByRoute);
                    if (seed != null) {
                        loaded.put(seed.routeKey(), seed);
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

    private CanonicalSignSeed importSeed(JsonNode node, Map<String, CanonicalSignSeed> legacySeedsByRoute) {
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

        CanonicalSignSeed legacy = legacySeedsByRoute.get(routeKey);
        JsonNode i18n = node.path("i18n");
        String descriptionEn = firstUsable(importText(i18n, "EN", "description"),
                legacy != null ? legacy.shortDescriptionEn() : null);
        String descriptionAr = firstUsable(importText(i18n, "AR", "description"),
                legacy != null ? legacy.shortDescriptionAr() : null);
        String descriptionNl = firstUsable(importText(i18n, "NL", "description"),
                legacy != null ? legacy.shortDescriptionNl() : null);
        String descriptionFr = firstUsable(importText(i18n, "FR", "description"),
                legacy != null ? legacy.shortDescriptionFr() : null);

        return new CanonicalSignSeed(
                routeCode,
                routeKey,
                signCode,
                normalizedCode,
                category,
                firstUsable(importText(i18n, "EN", "name"), legacy != null ? legacy.nameEn() : null),
                firstUsable(importText(i18n, "AR", "name"), legacy != null ? legacy.nameAr() : null),
                firstUsable(importText(i18n, "NL", "name"), legacy != null ? legacy.nameNl() : null),
                firstUsable(importText(i18n, "FR", "name"), legacy != null ? legacy.nameFr() : null),
                descriptionEn,
                descriptionAr,
                descriptionNl,
                descriptionFr,
                firstUsable(legacy != null ? legacy.longDescriptionEn() : null, descriptionEn),
                firstUsable(legacy != null ? legacy.longDescriptionAr() : null, descriptionAr),
                firstUsable(legacy != null ? legacy.longDescriptionNl() : null, descriptionNl),
                firstUsable(legacy != null ? legacy.longDescriptionFr() : null, descriptionFr),
                imagePath);
    }

    private Set<String> loadAllowedImagePaths() {
        try (InputStream input = openInputStream(canonicalImagesPath)) {
            List<String> items = objectMapper.readValue(input, new TypeReference<List<String>>() {
            });
            Set<String> normalized = new LinkedHashSet<>();
            for (String item : items) {
                String path = normalizeImagePath(item);
                if (!path.isBlank()) {
                    normalized.add(path);
                }
            }
            return normalized;
        } catch (IOException ex) {
            log.error("Failed to load canonical sign image manifest from {}: {}", canonicalImagesPath, ex.getMessage(),
                    ex);
            return Set.of();
        }
    }

    private InputStream openInputStream(String path) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + path);
        if (resource.exists()) {
            return resource.getInputStream();
        }

        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("user.dir"), path);
        }
        return new FileInputStream(file);
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
            String shortDescriptionEn,
            String shortDescriptionAr,
            String shortDescriptionNl,
            String shortDescriptionFr,
            String longDescriptionEn,
            String longDescriptionAr,
            String longDescriptionNl,
            String longDescriptionFr,
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
            String longDescriptionEn,
            String longDescriptionNl,
            String longDescriptionFr,
            String longDescriptionAr,
            boolean hasLongDescription,
            String imagePath,
            boolean publiclyAllowed) {
    }
}
