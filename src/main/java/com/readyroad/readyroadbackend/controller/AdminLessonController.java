package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.DraftResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.LessonDetail;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.LessonSummary;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.SaveDraftRequest;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.PublishRequest;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.TheoryCategoryResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.VersionSummary;
import com.readyroad.readyroadbackend.service.AdminLessonService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lessons")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminLessonController {

    private final AdminLessonService adminLessonService;
    private final AuthenticationUtil authenticationUtil;

    @GetMapping
    public ResponseEntity<List<LessonSummary>> listLessons() {

        return ResponseEntity.ok(
                adminLessonService.listLessons());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<TheoryCategoryResponse>> categories() {

        return ResponseEntity.ok(
                adminLessonService.getTheoryCategories());
    }

    @GetMapping("/{idOrCode}")
    public ResponseEntity<LessonDetail> getLesson(
            @PathVariable String idOrCode) {

        return ResponseEntity.ok(
                adminLessonService.getLesson(idOrCode));
    }

    @PostMapping("/{idOrCode}/draft")
    public ResponseEntity<DraftResponse> getOrCreateDraft(
            @PathVariable String idOrCode,
            Authentication authentication) {

        Long actorUserId =
                authenticationUtil.extractUserId(authentication);

        return ResponseEntity.ok(
                adminLessonService.getOrCreateDraft(
                        idOrCode,
                        actorUserId));
    }

    @PutMapping("/{idOrCode}/draft")
    public ResponseEntity<DraftResponse> saveDraft(
            @PathVariable String idOrCode,
            @Valid @RequestBody SaveDraftRequest request,
            Authentication authentication) {

        Long actorUserId =
                authenticationUtil.extractUserId(authentication);

        return ResponseEntity.ok(
                adminLessonService.saveDraft(
                        idOrCode,
                        request.expectedRevision(),
                        request.document(),
                        actorUserId));
    }

    @PostMapping("/{idOrCode}/publish")
    public ResponseEntity<VersionSummary> publish(
            @PathVariable String idOrCode,
            @Valid @RequestBody PublishRequest request,
            Authentication authentication) {

        Long actorUserId =
                authenticationUtil.extractUserId(authentication);

        return ResponseEntity.ok(
                adminLessonService.publishDraft(
                        idOrCode,
                        request.expectedRevision(),
                        request.changeNote(),
                        actorUserId));
    }

    @GetMapping("/{idOrCode}/versions")
    public ResponseEntity<List<VersionSummary>> versions(
            @PathVariable String idOrCode) {

        return ResponseEntity.ok(
                adminLessonService.getVersionHistory(idOrCode));
    }
}