package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/marketing/editorial")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class EditorialAdminController {

    private final EditorialBacklogService service;
    private final EditorialPriorityAdminService priorityAdminService;
    private final EditorialSourceCollectionService sourceCollectionService;
    private final EditorialEditorService editorService;

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

    @GetMapping("/editor")
    public EditorialEditorDtos.Workspace editor() {
        return editorService.workspace();
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
}
