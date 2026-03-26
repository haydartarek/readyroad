package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    @Value("${readyroad.signs.canonical-path:data/signs.json}")
    private String canonicalSignsPath;

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

        return new ResolvedSignData(
                firstUsable(seed != null ? seed.signCode() : null, sign.getSignCode()),
                routeCodeFor(sign),
                firstUsable(seed != null ? seed.nameEn() : null, sign.getNameEn(), sign.getSignCode()),
                firstUsable(seed != null ? seed.nameAr() : null, sign.getNameAr(), sign.getNameEn(),
                        sign.getSignCode()),
                firstUsable(seed != null ? seed.nameNl() : null, sign.getNameNl(), sign.getNameEn(),
                        sign.getSignCode()),
                firstUsable(seed != null ? seed.nameFr() : null, sign.getNameFr(), sign.getNameEn(),
                        sign.getSignCode()),
                firstUsable(seed != null ? seed.shortDescriptionEn() : null, sign.getDescriptionEn()),
                firstUsable(seed != null ? seed.shortDescriptionAr() : null, sign.getDescriptionAr()),
                firstUsable(seed != null ? seed.shortDescriptionNl() : null, sign.getDescriptionNl()),
                firstUsable(seed != null ? seed.shortDescriptionFr() : null, sign.getDescriptionFr()),
                firstUsable(seed != null ? seed.longDescriptionEn() : null, sign.getDescriptionEn()),
                firstUsable(seed != null ? seed.longDescriptionNl() : null, sign.getDescriptionNl()),
                firstUsable(seed != null ? seed.longDescriptionFr() : null, sign.getDescriptionFr()),
                firstUsable(seed != null ? seed.longDescriptionAr() : null, sign.getDescriptionAr()),
                !isBlank(seed != null ? seed.longDescriptionEn() : null)
                        || !isBlank(seed != null ? seed.longDescriptionNl() : null)
                        || !isBlank(seed != null ? seed.longDescriptionFr() : null)
                        || !isBlank(seed != null ? seed.longDescriptionAr() : null),
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
        sign.setDescriptionEn(seed.shortDescriptionEn());
        sign.setDescriptionAr(seed.shortDescriptionAr());
        sign.setDescriptionNl(seed.shortDescriptionNl());
        sign.setDescriptionFr(seed.shortDescriptionFr());
        if (!seed.imagePath().isBlank()) {
            sign.setImagePath(stripLeadingSlash(seed.imagePath()));
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
        try (InputStream input = openInputStream(canonicalSignsPath)) {
            List<JsonNode> entries = objectMapper.readValue(input, new TypeReference<List<JsonNode>>() {
            });
            List<CanonicalSignSeed> loaded = new ArrayList<>();

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

                loaded.add(new CanonicalSignSeed(
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
            return List.of();
        }
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
        return CATEGORY_NAME_TO_ENUM.get(value.trim().toLowerCase(Locale.ROOT));
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

    private static String stripLeadingSlash(String value) {
        return value != null && value.startsWith("/") ? value.substring(1) : value;
    }

    private static String text(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        String value = node.get(field).asText();
        return value == null ? null : value.trim();
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
