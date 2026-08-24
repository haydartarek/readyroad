package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/marketing/editorial")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class EditorialAdminController {

    private final EditorialBacklogService service;
    private final EditorialPriorityAdminService priorityAdminService;
    private final EditorialSourceCollectionService sourceCollectionService;
    private final EditorialBriefService briefService;
    private final EditorialDraftService draftService;
    private final EditorialEditorService editorService;
    private final EditorialArticleApprovalService articleApprovalService;
    private final EditorialArticleImageService articleImageService;
    private final EditorialPerformanceService performanceService;

    @GetMapping("/backlog")
    public EditorialDtos.Backlog backlog() {
        return service.backlog();
    }

    @GetMapping("/priorities")
    public List<EditorialDtos.Priority> priorities() {
        return priorityAdminService.priorities();
    }

    @GetMapping("/priority-settings")
    public Map<String, Object> prioritySettings() {
        return priorityAdminService.settings();
    }

    @PostMapping("/priorities/recalculate")
    public ResponseEntity<MarketingTaskLifecycleResponse> recalculate(
            @Valid @RequestBody EditorialDtos.RecalculateRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(
                priorityAdminService.requestRecalculation(request.idempotencyKey(), principal.getName()));
    }

    @PutMapping("/priority-settings")
    public ResponseEntity<MarketingTaskLifecycleResponse> updatePrioritySettings(
            @Valid @RequestBody EditorialDtos.SettingsUpdateRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(
                priorityAdminService.requestSettingsUpdate(request, principal.getName()));
    }

    @GetMapping("/sources")
    public List<EditorialSourceDtos.Source> sources(
            @RequestParam(required = false) Long articleTopicId) {
        return sourceCollectionService.sources(articleTopicId);
    }

    @PostMapping("/source-collections")
    public ResponseEntity<MarketingTaskLifecycleResponse> collectSources(
            @Valid @RequestBody EditorialSourceDtos.SourceCollectionRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(
                sourceCollectionService.request(request, principal.getName()));
    }

    @PostMapping("/topics/{topicId}/briefs")
    public ResponseEntity<MarketingTaskLifecycleResponse> createBrief(
            @PathVariable long topicId,
            @Valid @RequestBody EditorialBriefDtos.CreateRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(
                briefService.request(topicId, request, principal.getName()));
    }

    @PostMapping("/editor/articles/{articleId}/draft-requests")
    public ResponseEntity<MarketingTaskLifecycleResponse> createDraft(
            @PathVariable long articleId,
            @Valid @RequestBody EditorialDraftDtos.CreateRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(
                draftService.request(articleId, request, principal.getName()));
    }

    @GetMapping("/editor")
    public EditorialEditorDtos.Workspace editor() {
        return editorService.workspace();
    }

    @GetMapping("/editor/topics/{topicId}/authoring-status")
    public EditorialEditorDtos.AuthoringStatus authoringStatus(@PathVariable long topicId) {
        return editorService.authoringStatus(topicId);
    }

    @GetMapping("/editor/articles/{articleId}/versions")
    public List<EditorialEditorDtos.Version> articleVersions(
            @PathVariable long articleId,
            @RequestParam String language) {
        return editorService.versions(articleId, language);
    }

    @PutMapping("/editor/topics/{topicId}/versions/{language}")
    public EditorialEditorDtos.SaveResult saveDraft(
            @PathVariable long topicId,
            @PathVariable String language,
            @Valid @RequestBody EditorialEditorDtos.SaveRequest request,
            Principal principal) {
        return editorService.save(topicId, language, request, principal.getName());
    }

    @GetMapping("/editor/articles/{articleId}/image")
    public ResponseEntity<EditorialArticleImageDtos.Asset> articleImage(
            @PathVariable long articleId) {
        return articleImageService.current(articleId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/editor/articles/{articleId}/performance")
    public EditorialPerformanceDtos.Overview articlePerformance(@PathVariable long articleId) {
        return performanceService.overview(articleId);
    }

    @PostMapping(
            value = "/editor/articles/{articleId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EditorialArticleImageDtos.Asset uploadArticleImage(
            @PathVariable long articleId,
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("metadata") EditorialArticleImageDtos.UploadMetadata metadata,
            Principal principal) {
        return articleImageService.upload(articleId, file, metadata, principal.getName());
    }

    @PostMapping("/editor/articles/{articleId}/approval-requests")
    public ResponseEntity<MarketingTaskLifecycleResponse> requestArticleApproval(
            @PathVariable long articleId,
            @Valid @RequestBody EditorialArticleApprovalDtos.Request request,
            Principal principal) {
        return ResponseEntity.accepted().body(
                articleApprovalService.request(articleId, request, principal.getName()));
    }
}
