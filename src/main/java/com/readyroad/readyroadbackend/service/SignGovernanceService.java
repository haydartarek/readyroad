package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.dto.SignGovernanceReport;
import com.readyroad.readyroadbackend.dto.SignGovernanceReport.AuditResult;
import com.readyroad.readyroadbackend.dto.SignGovernanceReport.SignAuditItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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

    private final TrafficSignRepository trafficSignRepository;
    private final ObjectMapper objectMapper;

    @Value("${readyroad.signs.canonical-path:data/signs.json}")
    private String canonicalPath;

    public SignGovernanceService(TrafficSignRepository trafficSignRepository, ObjectMapper objectMapper) {
        this.trafficSignRepository = trafficSignRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Perform a full canonical-source governance audit.
     * Compares every DB sign against signs.json and vice versa.
     */
    public AuditResult audit() {
        Map<String, JsonNode> jsonSignsByCode = loadCanonicalSigns();
        List<TrafficSign> dbSigns = trafficSignRepository.findAll();
        Map<String, TrafficSign> dbSignsByCode = dbSigns.stream()
                .collect(Collectors.toMap(TrafficSign::getSignCode, s -> s, (a, b) -> a));

        List<SignAuditItem> details = new ArrayList<>();
        int fullyConsistent = 0;
        int withIssues = 0;
        int orphanInDb = 0;
        int orphanInJson = 0;

        // Check every DB sign against JSON
        for (TrafficSign dbSign : dbSigns) {
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
    private List<String> compareSign(TrafficSign dbSign, JsonNode jsonSign) {
        List<String> issues = new ArrayList<>();

        compareField(issues, dbSign.getSignCode(), "long_description_en",
                dbSign.getLongDescriptionEn(), textOrNull(jsonSign, "long_description_en"));
        compareField(issues, dbSign.getSignCode(), "long_description_nl",
                dbSign.getLongDescriptionNl(), textOrNull(jsonSign, "long_description_nl"));
        compareField(issues, dbSign.getSignCode(), "long_description_fr",
                dbSign.getLongDescriptionFr(), textOrNull(jsonSign, "long_description_fr"));
        compareField(issues, dbSign.getSignCode(), "long_description_ar",
                dbSign.getLongDescriptionAr(), textOrNull(jsonSign, "long_description_ar"));

        // Check completeness
        if (!dbSign.isLongDescriptionComplete()) {
            issues.add("isLongDescriptionComplete=false — at least one long_description field is null or empty");
        }

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
        try {
            File file = new File(canonicalPath);
            if (!file.isAbsolute()) {
                // Resolve relative to working directory
                file = new File(System.getProperty("user.dir"), canonicalPath);
            }
            if (!file.exists()) {
                log.warn("Canonical signs.json not found at: {}", file.getAbsolutePath());
                return Map.of();
            }
            List<JsonNode> signs = objectMapper.readValue(file, new TypeReference<List<JsonNode>>() {
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

    private static String textOrNull(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asText();
    }
}
