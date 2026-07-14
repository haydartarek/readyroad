package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.util.DrivingTextSanitizer;
import com.readyroad.readyroadbackend.util.ImportedTextSanitizer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Replaces raw road-sign codes in user-facing text with localized sign names or
 * human-readable aliases. Canonical names come from the database rows derived
 * from signs_import; a small curated alias list covers non-canonical references
 * such as M1/M2/M3 or family references like B15.
 */
@Component
@RequiredArgsConstructor
public class RoadSignReferenceTextResolver {

    private static final Logger log = LoggerFactory.getLogger(RoadSignReferenceTextResolver.class);

    private static final long CACHE_TTL_MILLIS = Duration.ofMinutes(5).toMillis();

    private final RoadSignRepository roadSignRepository;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Value("${readyroad.signs.reference-aliases-path:data/sign_reference_aliases.json}")
    private String referenceAliasesPath = "data/sign_reference_aliases.json";

    private volatile CachedCatalog cachedCatalog;

    public String resolveEn(String value) {
        return resolve(value, Language.EN);
    }

    public String resolveAr(String value) {
        return resolve(value, Language.AR);
    }

    public String resolveNl(String value) {
        return resolve(value, Language.NL);
    }

    public String resolveFr(String value) {
        return resolve(value, Language.FR);
    }

    private String resolve(String value, Language language) {
        String sanitized = ImportedTextSanitizer.sanitize(value);
        if (sanitized == null || sanitized.isBlank()) {
            return sanitized;
        }

        CachedCatalog catalog = getCatalog();
        if (catalog.pattern == null) {
            return sanitized;
        }

        Matcher matcher = catalog.pattern.matcher(sanitized);
        if (!matcher.find()) {
            return sanitized;
        }

        StringBuffer output = new StringBuffer();
        do {
            String token = matcher.group(1);
            String replacement = catalog.getLocalizedName(language, token);
            matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
        } while (matcher.find());
        matcher.appendTail(output);

        String resolved = output.toString()
                .replaceAll("\\s{2,}", " ")
                .replaceAll("\\s+([.,;:!?])", "$1")
                .trim();

        return DrivingTextSanitizer.sanitize(language.name(), resolved);
    }

    private CachedCatalog getCatalog() {
        CachedCatalog local = cachedCatalog;
        long now = System.currentTimeMillis();
        if (local != null && (now - local.loadedAtMillis) < CACHE_TTL_MILLIS) {
            return local;
        }

        synchronized (this) {
            local = cachedCatalog;
            if (local != null && (now - local.loadedAtMillis) < CACHE_TTL_MILLIS) {
                return local;
            }

            CachedCatalog refreshed = buildCatalog(now);
            cachedCatalog = refreshed;
            return refreshed;
        }
    }

    private CachedCatalog buildCatalog(long loadedAtMillis) {
        List<RoadSign> signs = new ArrayList<>(roadSignRepository.findAllByIsActiveTrue());
        signs.sort(Comparator.comparingInt((RoadSign sign) -> safe(sign.getSignCode()).length()).reversed());

        Map<String, SignNames> namesByCode = new LinkedHashMap<>();
        for (RoadSign sign : signs) {
            String codeKey = normalizeCodeKey(sign.getSignCode());
            if (codeKey.isBlank()) {
                continue;
            }

            SignNames names = SignNames.fromSign(sign, codeKey);
            namesByCode.putIfAbsent(codeKey, names);
        }

        mergeCustomAliases(namesByCode);

        Pattern pattern = null;
        if (!namesByCode.isEmpty()) {
            String alternation = namesByCode.keySet().stream()
                    .sorted(Comparator.comparingInt(String::length).reversed())
                    .map(Pattern::quote)
                    .collect(Collectors.joining("|"));

            pattern = Pattern.compile(
                    "(?<![A-Za-z0-9_-])((?:" + alternation + ")(?:[-_][A-Za-z0-9]+)*)(?![A-Za-z0-9_-])",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        }

        return new CachedCatalog(pattern, Map.copyOf(namesByCode), loadedAtMillis);
    }

    private void mergeCustomAliases(Map<String, SignNames> namesByCode) {
        for (CustomAlias alias : loadCustomAliases(referenceAliasesPath)) {
            String aliasKey = normalizeCodeKey(alias.code());
            if (aliasKey.isBlank() || namesByCode.containsKey(aliasKey)) {
                continue;
            }

            SignNames referencedNames = namesByCode.get(normalizeCodeKey(alias.targetCode()));
            if (referencedNames != null) {
                namesByCode.put(aliasKey, referencedNames);
                continue;
            }

            SignNames explicitNames = alias.toNames(aliasKey);
            if (explicitNames != null) {
                namesByCode.put(aliasKey, explicitNames);
            }
        }
    }

    private List<CustomAlias> loadCustomAliases(String configuredPath) {
        try (InputStream input = openInputStream(configuredPath)) {
            return objectMapper.readValue(input, new TypeReference<List<CustomAlias>>() {
            });
        } catch (IOException ex) {
            log.warn("Unable to load sign reference aliases from {}: {}", configuredPath, ex.getMessage());
            return List.of();
        }
    }

    private InputStream openInputStream(String configuredPath) throws IOException {
        if (configuredPath == null || configuredPath.isBlank()) {
            throw new IOException("Configured path is blank");
        }

        Resource resource = configuredPath.startsWith("classpath:")
                ? resourceLoader.getResource(configuredPath)
                : resourceLoader.getResource("classpath:" + configuredPath);
        if (resource.exists()) {
            return resource.getInputStream();
        }

        File file = new File(configuredPath);
        if (file.exists()) {
            return new FileInputStream(file);
        }

        Resource fallback = resourceLoader.getResource(configuredPath);
        if (fallback.exists()) {
            return fallback.getInputStream();
        }

        throw new IOException("Resource not found: " + configuredPath);
    }

    private static String fallbackName(String... values) {
        for (String value : values) {
            String sanitized = safe(value);
            if (!sanitized.isBlank()) {
                return sanitized;
            }
        }
        return "";
    }

    private static String normalizeCodeKey(String value) {
        return safe(value).toUpperCase(Locale.ROOT);
    }

    private static String normalizeLegacyAliasKey(String value) {
        return normalizeCodeKey(value)
                .replace("-RECHTSAF", "-RIGHT")
                .replace("-RECHTSDOOR", "-STRAIGHT")
                .replace("-RECHTS", "-RIGHT")
                .replace("-LINKSAF", "-LEFT")
                .replace("-LINKS", "-LEFT")
                .replace("-REEKS", "-SERIES")
                .replace("-SERIE", "-SERIES")
                .replace("_RECHTSAF", "_RIGHT")
                .replace("_RECHTSDOOR", "_STRAIGHT")
                .replace("_RECHTS", "_RIGHT")
                .replace("_LINKSAF", "_LEFT")
                .replace("_LINKS", "_LEFT")
                .replace("_REEKS", "_SERIES")
                .replace("_SERIE", "_SERIES");
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private enum Language {
        EN,
        AR,
        NL,
        FR
    }

    private record CachedCatalog(
            Pattern pattern,
            Map<String, SignNames> namesByCode,
            long loadedAtMillis) {

        private String getLocalizedName(Language language, String token) {
            String normalizedToken = normalizeCodeKey(token);
            SignNames names = namesByCode.get(normalizedToken);
            if (names == null) {
                names = namesByCode.get(normalizeLegacyAliasKey(normalizedToken));
            }
            if (names == null) {
                CompoundMatch compound = findCompoundMatch(normalizedToken);
                if (compound != null) {
                    return compound.toLocalizedLabel(language);
                }
            }
            if (names == null) {
                return token;
            }

            return switch (language) {
                case EN -> names.en();
                case AR -> names.ar();
                case NL -> names.nl();
                case FR -> names.fr();
            };
        }

        private CompoundMatch findCompoundMatch(String normalizedToken) {
            if (!normalizedToken.contains("-") && !normalizedToken.contains("_")) {
                return null;
            }

            String[] parts = normalizedToken.split("[-_]");
            if (parts.length < 2) {
                return null;
            }

            for (int prefixLength = parts.length - 1; prefixLength >= 1; prefixLength--) {
                String prefix = String.join("-", java.util.Arrays.copyOfRange(parts, 0, prefixLength));
                String suffix = String.join("-", java.util.Arrays.copyOfRange(parts, prefixLength, parts.length));
                if (suffix.isBlank()) {
                    continue;
                }

                SignNames prefixNames = namesByCode.get(prefix);
                if (prefixNames == null) {
                    prefixNames = namesByCode.get(normalizeLegacyAliasKey(prefix));
                }
                if (prefixNames == null) {
                    continue;
                }

                String normalizedSuffix = normalizeLegacyAliasKey(suffix);
                if (normalizedSuffix.matches("\\d+")) {
                    continue;
                }

                return new CompoundMatch(prefixNames, normalizedSuffix);
            }

            return null;
        }

        private record CompoundMatch(SignNames names, String suffix) {

            private String toLocalizedLabel(Language language) {
                String baseName = switch (language) {
                    case EN -> names.en();
                    case AR -> names.ar();
                    case NL -> names.nl();
                    case FR -> names.fr();
                };

                return switch (classifySuffix(suffix)) {
                    case SIGN -> switch (language) {
                        case EN -> "the sign " + baseName;
                        case AR -> "العلامة " + baseName;
                        case NL -> "het bord " + baseName;
                        case FR -> "le panneau " + baseName;
                    };
                    case SIGNS -> switch (language) {
                        case EN -> "the traffic signs indicated by " + baseName;
                        case AR -> "العلامات المرورية المشار إليها بعلامة " + baseName;
                        case NL -> "de verkeersborden aangeduid met " + baseName;
                        case FR -> "les panneaux indiqués par " + baseName;
                    };
                    case ZONE -> switch (language) {
                        case EN -> "the zone marked by " + baseName;
                        case AR -> "المنطقة المشار إليها بعلامة " + baseName;
                        case NL -> "de zone aangeduid met " + baseName;
                        case FR -> "la zone indiquee par " + baseName;
                    };
                    case PARKING_SPOT -> switch (language) {
                        case EN -> "the parking space marked by " + baseName;
                        case AR -> "موقف الوقوف المشار إليه بعلامة " + baseName;
                        case NL -> "de parkeerplaats aangeduid met " + baseName;
                        case FR -> "la place de stationnement indiquee par " + baseName;
                    };
                    case PATH -> switch (language) {
                        case EN -> "the path marked by " + baseName;
                        case AR -> "المسار المشار إليه بعلامة " + baseName;
                        case NL -> "het pad aangeduid met " + baseName;
                        case FR -> "le chemin indique par " + baseName;
                    };
                    case ROAD -> switch (language) {
                        case EN -> "the road marked by " + baseName;
                        case AR -> "الطريق المشار إليه بعلامة " + baseName;
                        case NL -> "de weg aangeduid met " + baseName;
                        case FR -> "la route indiquee par " + baseName;
                    };
                    case LANE -> switch (language) {
                        case EN -> "the lane marked by " + baseName;
                        case AR -> "المسلك المشار إليه بعلامة " + baseName;
                        case NL -> "de rijstrook aangeduid met " + baseName;
                        case FR -> "la voie indiquee par " + baseName;
                    };
                    case BOX -> switch (language) {
                        case EN -> "the waiting area marked by " + baseName;
                        case AR -> "منطقة الانتظار المشار إليها بعلامة " + baseName;
                        case NL -> "het vak aangeduid met " + baseName;
                        case FR -> "la zone d'attente indiquee par " + baseName;
                    };
                    case CROSSING -> switch (language) {
                        case EN -> "the crossing marked by " + baseName;
                        case AR -> "المعبر المشار إليه بعلامة " + baseName;
                        case NL -> "de oversteekplaats aangeduid met " + baseName;
                        case FR -> "le passage indique par " + baseName;
                    };
                    case LIMIT -> switch (language) {
                        case EN -> "the limit indicated by " + baseName;
                        case AR -> "الحد المشار إليه بعلامة " + baseName;
                        case NL -> "de limiet aangeduid met " + baseName;
                        case FR -> "la limite indiquee par " + baseName;
                    };
                    case SIDE -> switch (language) {
                        case EN -> "the side indicated by " + baseName;
                        case AR -> "الجانب المشار إليه بعلامة " + baseName;
                        case NL -> "de zijde aangeduid met " + baseName;
                        case FR -> "le cote indique par " + baseName;
                    };
                    case PERIOD -> switch (language) {
                        case EN -> "the period indicated by " + baseName;
                        case AR -> "الفترة المشار إليها بعلامة " + baseName;
                        case NL -> "de periode aangeduid met " + baseName;
                        case FR -> "la periode indiquee par " + baseName;
                    };
                    case TEMPORARY -> switch (language) {
                        case EN -> "the temporary arrangement marked by " + baseName;
                        case AR -> "الترتيب المؤقت المشار إليه بعلامة " + baseName;
                        case NL -> "de tijdelijke regeling aangeduid met " + baseName;
                        case FR -> "la signalisation temporaire indiquee par " + baseName;
                    };
                    case GENERIC -> baseName;
                };
            }

            private static CompoundSuffixKind classifySuffix(String suffix) {
                String normalized = suffix == null ? "" : suffix.toUpperCase(Locale.ROOT);
                if (normalized.isBlank()) {
                    return CompoundSuffixKind.GENERIC;
                }
                if (normalized.contains("BORDEN") || normalized.contains("SERIES")) {
                    return CompoundSuffixKind.SIGNS;
                }
                if (normalized.contains("BORD") || normalized.contains("TEKEN")) {
                    return CompoundSuffixKind.SIGN;
                }
                if (normalized.contains("ZONE")) {
                    return CompoundSuffixKind.ZONE;
                }
                if (normalized.contains("PLEK") || normalized.contains("PARKERPLAATS")) {
                    return CompoundSuffixKind.PARKING_SPOT;
                }
                if (normalized.contains("PAD")) {
                    return CompoundSuffixKind.PATH;
                }
                if (normalized.contains("BAAN") || normalized.contains("STRAAT")) {
                    return CompoundSuffixKind.LANE;
                }
                if (normalized.contains("WEG")) {
                    return CompoundSuffixKind.ROAD;
                }
                if (normalized.contains("VAK")) {
                    return CompoundSuffixKind.BOX;
                }
                if (normalized.contains("OVERSTEEKPLAATS")) {
                    return CompoundSuffixKind.CROSSING;
                }
                if (normalized.contains("LIMIET")) {
                    return CompoundSuffixKind.LIMIT;
                }
                if (normalized.contains("KANT")) {
                    return CompoundSuffixKind.SIDE;
                }
                if (normalized.contains("PERIODE")) {
                    return CompoundSuffixKind.PERIOD;
                }
                if (normalized.contains("TIJDELIJKE")) {
                    return CompoundSuffixKind.TEMPORARY;
                }
                return CompoundSuffixKind.GENERIC;
            }
        }

        private enum CompoundSuffixKind {
            SIGN,
            SIGNS,
            ZONE,
            PARKING_SPOT,
            PATH,
            ROAD,
            LANE,
            BOX,
            CROSSING,
            LIMIT,
            SIDE,
            PERIOD,
            TEMPORARY,
            GENERIC
        }
    }

    private record SignNames(String nl, String en, String fr, String ar) {

        private static SignNames fromSign(RoadSign sign, String fallbackCode) {
            return new SignNames(
                    fallbackName(sign.getNameNl(), sign.getNameEn(), sign.getNameFr(), sign.getNameAr(), fallbackCode),
                    fallbackName(sign.getNameEn(), sign.getNameNl(), sign.getNameFr(), sign.getNameAr(), fallbackCode),
                    fallbackName(sign.getNameFr(), sign.getNameEn(), sign.getNameNl(), sign.getNameAr(), fallbackCode),
                    fallbackName(sign.getNameAr(), sign.getNameEn(), sign.getNameFr(), sign.getNameNl(), fallbackCode));
        }
    }

    private record CustomAlias(
            String code,
            String targetCode,
            String titleNl,
            String titleEn,
            String titleFr,
            String titleAr) {

        private SignNames toNames(String fallbackCode) {
            String nl = fallbackName(titleNl, titleEn, titleFr, titleAr, fallbackCode);
            String en = fallbackName(titleEn, titleNl, titleFr, titleAr, fallbackCode);
            String fr = fallbackName(titleFr, titleEn, titleNl, titleAr, fallbackCode);
            String ar = fallbackName(titleAr, titleEn, titleFr, titleNl, fallbackCode);

            if (nl.isBlank() && en.isBlank() && fr.isBlank() && ar.isBlank()) {
                return null;
            }

            return new SignNames(nl, en, fr, ar);
        }
    }
}
