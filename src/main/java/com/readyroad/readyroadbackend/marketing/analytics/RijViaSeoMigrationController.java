package com.readyroad.readyroadbackend.marketing.analytics;

import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/marketing/seo-migration")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RijViaSeoMigrationController {

    private final RijViaSeoMigrationService service;

    @GetMapping("/workspace")
    public Map<String, Object> workspace() {
        return service.workspace();
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RijViaSeoMigrationStore.ImportResult> importWorkbook(
            @RequestPart("file") MultipartFile file,
            Principal principal) {
        RijViaSeoMigrationStore.ImportResult result =
                service.importWorkbook(file, principal.getName());
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK).body(result);
    }
}
