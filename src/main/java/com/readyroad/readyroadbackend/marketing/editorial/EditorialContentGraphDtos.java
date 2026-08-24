package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.List;

public final class EditorialContentGraphDtos {

    private EditorialContentGraphDtos() {}

    public record Graph(
            int articleNodeCount,
            int assetNodeCount,
            int edgeCount,
            int orphanArticleCount,
            List<Node> nodes,
            List<Edge> edges,
            List<OrphanArticle> orphanArticles) {}

    public record Node(
            String id,
            String type,
            String label,
            String language,
            String path,
            boolean published) {}

    public record Edge(
            String sourceId,
            String targetId,
            String type,
            String targetPath,
            String anchorText) {}

    public record OrphanArticle(
            long articleId,
            String language,
            String title,
            String lifecycleState,
            String reason) {}
}
