package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import java.time.Instant;
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
        if (!"IMAGE_REQUIRED".equals(article.lifecycleState())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Article images can only be approved in IMAGE_REQUIRED state");
        }

        var processed = processor.process(
                file,
                article.canonicalKey(),
                metadata.contentType(),
                metadata.focalPointX(),
                metadata.focalPointY());
        registerRollbackCleanup(processed);
        if (store.duplicate(processed.sha256(), metadata.sourcePlatform(), metadata.sourceAssetId())) {
            processor.delete(processed);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This source image is already registered in the editorial library");
        }

        long assetId = store.insertPending(articleId, processed, metadata);
        store.insertVariants(assetId, processed.variants());
        store.insertLocalizations(assetId, metadata);
        Instant approvedAt = Instant.now();
        long licenseId = store.insertLicense(assetId, articleId, metadata, approvedAt);
        store.activate(articleId, assetId);

        ObjectNode details = objectMapper.createObjectNode();
        details.put("articleId", articleId);
        details.put("imageAssetId", assetId);
        details.put("licenseRecordId", licenseId);
        details.put("sourcePlatform", metadata.sourcePlatform().name());
        details.put("sourceAssetId", metadata.sourceAssetId());
        details.put("variantCount", processed.variants().size());
        auditService.recordEntityEvent(
                AUDIT_EVENT,
                metadata.approvedBy(),
                "EDITORIAL_ARTICLE_IMAGE",
                String.valueOf(assetId),
                null,
                "editorial-image-" + assetId,
                details);
        return store.current(articleId).orElseThrow();
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
