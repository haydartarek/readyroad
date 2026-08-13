package com.readyroad.readyroadbackend.marketing.editorial;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/marketing/editorial")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class EditorialAdminController {

    private final EditorialBacklogService service;

    @GetMapping("/backlog")
    public EditorialDtos.Backlog backlog() {
        return service.backlog();
    }
}
