package com.readyroad.readyroadbackend.marketing.editorial;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EditorialInternalLinkPolicy {

    private static final Set<String> LANGUAGES = Set.of("AR", "NL", "FR", "EN");
    private static final Map<String, String> PREFIXES = Map.of(
            "AR", "/ar",
            "NL", "/nl",
            "FR", "/fr",
            "EN", "");
    private static final Set<String> GENERIC_ANCHORS = Set.of(
            "click here", "klik hier", "cliquez ici", "اضغط هنا");

    private final EditorialInternalLinkStore store;

    List<EditorialInternalLinkDtos.Link> normalize(
            long sourceArticleId,
            String language,
            List<EditorialInternalLinkDtos.Input> requestedLinks) {
        String normalizedLanguage = language(language);
        if (requestedLinks == null || requestedLinks.isEmpty()) {
            return List.of();
        }

        Set<String> uniqueTargets = new HashSet<>();
        List<EditorialInternalLinkDtos.Link> links = new ArrayList<>();
        for (EditorialInternalLinkDtos.Input requested : requestedLinks) {
            if (requested == null) {
                throw new IllegalArgumentException("Internal link is required");
            }
            String targetPath = cleanPath(requested.targetPath());
            String anchorText = anchor(requested.anchorText());
            if (!uniqueTargets.add(targetPath.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Duplicate internal link target: " + targetPath);
            }
            links.add(resolve(sourceArticleId, normalizedLanguage, targetPath, anchorText));
        }
        return List.copyOf(links);
    }

    private EditorialInternalLinkDtos.Link resolve(
            long sourceArticleId,
            String language,
            String targetPath,
            String anchorText) {
        String localizedPath = unprefixedPath(language, targetPath);
        String[] segments = localizedPath.substring(1).split("/");

        if (segments.length == 2 && "blog".equals(segments[0])) {
            long targetArticleId = store.publishedArticleId(language, decode(segments[1]))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Internal article target is not published in " + language + ": " + targetPath));
            if (targetArticleId == sourceArticleId) {
                throw new IllegalArgumentException("An article cannot link to itself");
            }
            return link("ARTICLE", targetPath, anchorText);
        }

        if ((segments.length == 2 || segments.length == 3) && "lessons".equals(segments[0])) {
            Integer pageNumber = segments.length == 3 ? positiveInteger(segments[2], targetPath) : null;
            if (!store.lessonExists(decode(segments[1]), pageNumber)) {
                throw new IllegalArgumentException("Unknown lesson target: " + targetPath);
            }
            return link("LESSON", targetPath, anchorText);
        }

        if (segments.length == 2 && "traffic-signs".equals(segments[0])) {
            if (!store.trafficSignExists(decode(segments[1]))) {
                throw new IllegalArgumentException("Unknown traffic sign target: " + targetPath);
            }
            return link("TRAFFIC_SIGN", targetPath, anchorText);
        }

        if ("practice".equals(segments[0]) && segments.length <= 2) {
            if (segments.length == 2
                    && !"random".equalsIgnoreCase(segments[1])
                    && !store.practiceCategoryExists(decode(segments[1]))) {
                throw new IllegalArgumentException("Unknown practice target: " + targetPath);
            }
            return link("PRACTICE", targetPath, anchorText);
        }

        if (segments.length == 1 && "exam".equals(segments[0])) {
            return link("EXAM", targetPath, anchorText);
        }
        if (segments.length == 1 && "videos".equals(segments[0])) {
            return link("VIDEO", targetPath, anchorText);
        }

        throw new IllegalArgumentException("Unsupported internal link target: " + targetPath);
    }

    private static EditorialInternalLinkDtos.Link link(
            String type,
            String targetPath,
            String anchorText) {
        return new EditorialInternalLinkDtos.Link(type, targetPath, anchorText);
    }

    private static String unprefixedPath(String language, String targetPath) {
        String expectedPrefix = PREFIXES.get(language);
        for (Map.Entry<String, String> entry : PREFIXES.entrySet()) {
            String prefix = entry.getValue();
            if (!prefix.isEmpty() && (targetPath.equals(prefix) || targetPath.startsWith(prefix + "/"))) {
                if (!entry.getKey().equals(language)) {
                    throw new IllegalArgumentException("Internal link language does not match article language");
                }
                return targetPath.substring(prefix.length());
            }
        }
        if (!expectedPrefix.isEmpty()) {
            throw new IllegalArgumentException("Localized internal link must use " + expectedPrefix);
        }
        return targetPath;
    }

    static String cleanPath(String value) {
        String normalized = value == null ? "" : value.trim();
        try {
            URI uri = new URI(normalized);
            if (normalized.isEmpty() || !normalized.startsWith("/") || normalized.startsWith("//")
                    || uri.isAbsolute() || uri.getHost() != null || uri.getQuery() != null
                    || uri.getFragment() != null || normalized.chars().anyMatch(Character::isWhitespace)) {
                throw new IllegalArgumentException("Internal link must be a clean local path");
            }
        } catch (URISyntaxException error) {
            throw new IllegalArgumentException("Internal link path is invalid", error);
        }
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            return normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private static String anchor(String value) {
        String normalized = value == null ? "" : value.trim().replaceAll("\\s+", " ");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Internal link anchor text is required");
        }
        if (GENERIC_ANCHORS.contains(normalized.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Internal link anchor text must describe its destination");
        }
        return normalized;
    }

    private static String language(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if (!LANGUAGES.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported article language: " + value);
        }
        return normalized;
    }

    private static Integer positiveInteger(String value, String targetPath) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 1) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Invalid lesson page target: " + targetPath, error);
        }
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
