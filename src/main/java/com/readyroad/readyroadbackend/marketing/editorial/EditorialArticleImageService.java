package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EditorialArticleImageService {

    static final String AUDIT_EVENT = "EDITORIAL_ARTICLE_IMAGE_APPROVED";
    static final String REMOVE_AUDIT_EVENT = "EDITORIAL_ARTICLE_IMAGE_REMOVED";

    private final EditorialArticleImagePolicy policy;
    private final EditorialArticleImageProcessor processor;
    private final EditorialArticleImageStore store;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<EditorialArticleImageDtos.Asset> current(long articleId) {
        if (articleId <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
        return store.current(articleId);
    }

    @Transactional
    public EditorialArticleImageDtos.Asset upload(
            long articleId,
            MultipartFile file,
            EditorialArticleImageDtos.UploadMetadata request,
            String actor) {
        if (articleId <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
        var metadata = policy.normalize(file, request, actor);
        var article = store.lockArticle(articleId);
        if (!EditorialArticleState.valueOf(article.lifecycleState()).allowsDraftPreparation()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Article images can only be changed in an editable draft or review state");
        }

        var processed = processor.process(
                file,
                metadata.storedFileName(),
                metadata.contentType(),
                0.5,
                0.5);
        registerRollbackCleanup(processed);
        if (store.duplicate(processed.sha256())) {
            processor.delete(processed);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This image is already registered in the editorial library");
        }

        long assetId = store.insertPending(articleId, processed, metadata);
        store.insertVariants(assetId, processed.variants());
        store.insertLocalizations(assetId, metadata);
        store.activate(articleId, assetId);

        ObjectNode details = objectMapper.createObjectNode();
        details.put("articleId", articleId);
        details.put("imageAssetId", assetId);
        details.put("contentSha256", processed.sha256());
        details.put("variantCount", processed.variants().size());
        auditService.recordEntityEvent(
                AUDIT_EVENT,
                metadata.uploadedBy(),
                "EDITORIAL_ARTICLE_IMAGE",
                String.valueOf(assetId),
                null,
                "editorial-image-" + assetId,
                details);
        return store.current(articleId).orElseThrow();
    }

    @Transactional
    public void remove(long articleId, String actor) {
        if (articleId <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid image remover is required");
        }
        var article = store.lockArticle(articleId);
        if (!EditorialArticleState.valueOf(article.lifecycleState()).allowsDraftPreparation()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Article images can only be removed in an editable draft or review state");
        }
        long assetId = store.supersedeCurrent(articleId);
        ObjectNode details = objectMapper.createObjectNode();
        details.put("articleId", articleId);
        details.put("imageAssetId", assetId);
        auditService.recordEntityEvent(
                REMOVE_AUDIT_EVENT,
                actor.trim(),
                "EDITORIAL_ARTICLE_IMAGE",
                String.valueOf(assetId),
                null,
                "editorial-image-remove-" + assetId,
                details);
    }

    private void registerRollbackCleanup(EditorialArticleImageProcessor.Processed processed) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    processor.delete(processed);
                }
            }
        });
    }
}
