package com.readyroad.readyroadbackend.marketing.analytics;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RijViaSeoMigrationService {

    private final MarketingProperties properties;
    private final SearchConsoleWorkbookParser parser;
    private final RijViaSeoOpportunityEngine opportunityEngine;
    private final RijViaSeoMigrationStore store;

    public Map<String, Object> workspace() {
        return store.latestWorkspace(properties.getLocalSeo().isImportEnabled());
    }

    public RijViaSeoMigrationStore.ImportResult importWorkbook(MultipartFile file, String actor) {
        if (!properties.getLocalSeo().isImportEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Local Search Console workbook import is disabled");
        }
        if (file == null || file.isEmpty()) {
            throw new InvalidSearchConsoleWorkbookException("A non-empty XLSX workbook is required");
        }
        if (file.getSize() > properties.getLocalSeo().getMaxUploadBytes()) {
            throw new InvalidSearchConsoleWorkbookException("The workbook exceeds the configured upload limit");
        }

        try {
            SearchConsoleWorkbookParser.ParsedWorkbook workbook =
                    parser.parse(file.getOriginalFilename(), file.getBytes());
            RijViaSeoOpportunityEngine.Analysis analysis = opportunityEngine.analyze(
                    workbook, properties.getLocalSeo().getCandidateDomain());
            return store.save(workbook, analysis, actor);
        } catch (IOException error) {
            throw new InvalidSearchConsoleWorkbookException("The XLSX workbook could not be read", error);
        }
    }
}
