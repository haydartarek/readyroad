package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EditorialContentGraphService {

    private final EditorialContentGraphStore store;

    @Transactional(readOnly = true)
    EditorialContentGraphDtos.Graph graph() {
        List<EditorialContentGraphStore.VersionRow> versions = store.currentVersions();
        Map<String, EditorialContentGraphDtos.Node> nodes = new LinkedHashMap<>();
        Map<String, String> articleNodeByPublishedPath = new LinkedHashMap<>();

        for (var version : versions) {
            String nodeId = articleNodeId(version.articleId(), version.language());
            String path = publishedPath(version.language(), version.publishedSlug());
            nodes.put(nodeId, new EditorialContentGraphDtos.Node(
                    nodeId,
                    version.pillar() ? "PILLAR_ARTICLE" : "SUPPORTING_ARTICLE",
                    version.title(),
                    version.language(),
                    path,
                    path != null));
            if (path != null) {
                articleNodeByPublishedPath.put(path.toLowerCase(Locale.ROOT), nodeId);
            }
        }

        List<EditorialContentGraphDtos.Edge> edges = new ArrayList<>();
        Set<String> connectedArticleNodes = new HashSet<>();
        for (var version : versions) {
            String sourceId = articleNodeId(version.articleId(), version.language());
            for (var link : EditorialArticleMetadata.internalLinks(version.metadata())) {
                String targetId = targetNodeId(link, articleNodeByPublishedPath);
                if (!nodes.containsKey(targetId)) {
                    boolean resolved = !targetId.startsWith("UNRESOLVED_ARTICLE:");
                    nodes.put(targetId, new EditorialContentGraphDtos.Node(
                            targetId,
                            resolved ? link.type() : "UNRESOLVED_ARTICLE",
                            link.anchorText(),
                            version.language(),
                            link.targetPath(),
                            resolved));
                }
                edges.add(new EditorialContentGraphDtos.Edge(
                        sourceId,
                        targetId,
                        link.type(),
                        link.targetPath(),
                        link.anchorText()));
                connectedArticleNodes.add(sourceId);
                if (targetId.startsWith("ARTICLE:")) {
                    connectedArticleNodes.add(targetId);
                }
            }
        }

        List<EditorialContentGraphDtos.OrphanArticle> orphans = versions.stream()
                .filter(version -> !connectedArticleNodes.contains(
                        articleNodeId(version.articleId(), version.language())))
                .map(version -> new EditorialContentGraphDtos.OrphanArticle(
                        version.articleId(),
                        version.language(),
                        version.title(),
                        version.lifecycleState(),
                        "NO_INBOUND_OR_OUTBOUND_LINKS"))
                .toList();
        int articleNodes = versions.size();
        return new EditorialContentGraphDtos.Graph(
                articleNodes,
                nodes.size() - articleNodes,
                edges.size(),
                orphans.size(),
                List.copyOf(nodes.values()),
                List.copyOf(edges),
                orphans);
    }

    private static String targetNodeId(
            EditorialInternalLinkDtos.Link link,
            Map<String, String> articleNodeByPublishedPath) {
        if ("ARTICLE".equals(link.type())) {
            return articleNodeByPublishedPath.getOrDefault(
                    link.targetPath().toLowerCase(Locale.ROOT),
                    "UNRESOLVED_ARTICLE:" + link.targetPath());
        }
        return link.type() + ":" + link.targetPath();
    }

    private static String articleNodeId(long articleId, String language) {
        return "ARTICLE:" + articleId + ":" + language;
    }

    private static String publishedPath(String language, String slug) {
        if (slug == null || slug.isBlank()) {
            return null;
        }
        String prefix = "EN".equals(language) ? "" : "/" + language.toLowerCase(Locale.ROOT);
        return prefix + "/blog/" + slug;
    }
}
