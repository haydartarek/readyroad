package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.dto.SignGovernanceReport.AuditResult;
import com.readyroad.readyroadbackend.dto.SignGovernanceReport.SignAuditItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Governance audit: compares signs.json (canonical source) against the
 * database.
 * <p>
 * This service is ONLY invoked by admin audit endpoints — never at public
 * request time.
 * It reads signs.json solely for consistency verification, not for serving
 * content.
 * </p>
 */
@Service
@Slf4j
public class SignGovernanceService {

    private final RoadSignRepository roadSignRepository;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Value("${readyroad.signs.canonical-path:data/signs.json}")
    private String canonicalPath;

    public SignGovernanceService(
            RoadSignRepository roadSignRepository,
            ObjectMapper objectMapper,
            ResourceLoader resourceLoader) {
        this.roadSignRepository = roadSignRepository;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Perform a full canonical-source governance audit.
     * Compares every DB sign against signs.json and vice versa.
     */
    public AuditResult audit() {
        Map<String, JsonNode> jsonSignsByCode = loadCanonicalSigns();
        List<RoadSign> dbSigns = roadSignRepository.findAll();
        Map<String, RoadSign> dbSignsByCode = dbSigns.stream()
                .collect(Collectors.toMap(RoadSign::getSignCode, s -> s, (a, b) -> a));

        List<SignAuditItem> details = new ArrayList<>();
        int fullyConsistent = 0;
        int withIssues = 0;
        int orphanInDb = 0;
        int orphanInJson = 0;

        // Check every DB sign against JSON
        for (RoadSign dbSign : dbSigns) {
            String code = dbSign.getSignCode();
            JsonNode jsonSign = jsonSignsByCode.get(code);
            if (jsonSign == null) {
                details.add(new SignAuditItem(code, "ORPHAN_IN_DB",
                        List.of("Sign exists in DB but not in signs.json")));
                orphanInDb++;
                withIssues++;
                continue;
            }
            List<String> issues = compareSign(dbSign, jsonSign);
            if (issues.isEmpty()) {
                fullyConsistent++;
            } else {
                details.add(new SignAuditItem(code, "MISMATCH", issues));
                withIssues++;
            }
        }

        // Check for JSON signs not in DB
        for (String jsonCode : jsonSignsByCode.keySet()) {
            if (!dbSignsByCode.containsKey(jsonCode)) {
                details.add(new SignAuditItem(jsonCode, "ORPHAN_IN_JSON",
                        List.of("Sign exists in signs.json but not in DB")));
                orphanInJson++;
                withIssues++;
            }
        }

        boolean passed = (withIssues == 0);
        log.info("Governance audit: {} DB signs, {} JSON signs, {} consistent, {} issues, passed={}",
                dbSigns.size(), jsonSignsByCode.size(), fullyConsistent, withIssues, passed);

        return new AuditResult(
                dbSigns.size(),
                jsonSignsByCode.size(),
                fullyConsistent,
                withIssues,
                orphanInDb,
                orphanInJson,
                passed,
                details);
    }

    /**
     * Compare a single DB sign's long_description fields against its JSON
     * counterpart.
     */
    private List<String> compareSign(RoadSign dbSign, JsonNode jsonSign) {
        List<String> issues = new ArrayList<>();

        compareField(issues, dbSign.getSignCode(), "description_en",
                dbSign.getDescriptionEn(), textOrNull(jsonSign, "long_description_en"));
        compareField(issues, dbSign.getSignCode(), "description_nl",
                dbSign.getDescriptionNl(), textOrNull(jsonSign, "long_description_nl"));
        compareField(issues, dbSign.getSignCode(), "description_fr",
                dbSign.getDescriptionFr(), textOrNull(jsonSign, "long_description_fr"));
        compareField(issues, dbSign.getSignCode(), "description_ar",
                dbSign.getDescriptionAr(), textOrNull(jsonSign, "long_description_ar"));

        return issues;
    }

    private void compareField(List<String> issues, String code, String field, String dbValue, String jsonValue) {
        if (jsonValue == null || jsonValue.isBlank()) {
            // JSON has no value — nothing to enforce
            return;
        }
        if (dbValue == null || dbValue.isBlank()) {
            issues.add(field + ": DB is null/empty but signs.json has a value");
            return;
        }
        if (!dbValue.equals(jsonValue)) {
            issues.add(field + ": DB does not match signs.json");
        }
    }

    /**
     * Load the canonical signs.json file and index entries by code.
     */
    private Map<String, JsonNode> loadCanonicalSigns() {
        try (InputStream input = openCanonicalInputStream()) {
            List<JsonNode> signs = objectMapper.readValue(input, new TypeReference<List<JsonNode>>() {
            });
            Map<String, JsonNode> byCode = new LinkedHashMap<>();
            for (JsonNode sign : signs) {
                String code = sign.has("code") ? sign.get("code").asText() : null;
                if (code != null && !code.isBlank()) {
                    byCode.put(code, sign);
                }
            }
            log.info("Loaded {} entries from canonical signs.json", byCode.size());
            return byCode;
        } catch (IOException e) {
            log.error("Failed to read canonical signs.json: {}", e.getMessage());
            return Map.of();
        }
    }

    private InputStream openCanonicalInputStream() throws IOException {
        Resource classpathResource = resourceLoader.getResource("classpath:" + canonicalPath);
        if (classpathResource.exists()) {
            return classpathResource.getInputStream();
        }

        File file = new File(canonicalPath);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("user.dir"), canonicalPath);
        }
        if (!file.exists()) {
            log.warn("Canonical signs.json not found at: {}", file.getAbsolutePath());
            throw new IOException("Canonical signs.json not found");
        }
        return new FileInputStream(file);
    }

    private static String textOrNull(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asText();
    }
}
