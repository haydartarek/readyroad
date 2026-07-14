package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.dto.SignGovernanceReport.AuditResult;
import com.readyroad.readyroadbackend.dto.SignGovernanceReport.SignAuditItem;
import com.readyroad.readyroadbackend.util.RouteCodeNormalizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Governance audit: compares signs_import (canonical source) against the
 * database.
 * <p>
 * This service is ONLY invoked by admin audit endpoints — never at public
 * request time.
 * It reuses the validated canonical catalog instead of parsing a second JSON
 * source.
 * </p>
 */
@Service
@Slf4j
public class SignGovernanceService {

    private final RoadSignRepository roadSignRepository;
    private final CanonicalSignCatalogService canonicalSignCatalogService;

    public SignGovernanceService(
            RoadSignRepository roadSignRepository,
            CanonicalSignCatalogService canonicalSignCatalogService) {
        this.roadSignRepository = roadSignRepository;
        this.canonicalSignCatalogService = canonicalSignCatalogService;
    }

    /**
     * Perform a full canonical-source governance audit.
     * Compares every DB sign against signs_import and vice versa.
     */
    public AuditResult audit() {
        Map<String, CanonicalSignCatalogService.CanonicalSignSeed> canonicalSignsByRoute =
                canonicalSignCatalogService.getCanonicalSeeds().stream()
                        .collect(Collectors.toMap(
                                CanonicalSignCatalogService.CanonicalSignSeed::routeKey,
                                seed -> seed,
                                (left, right) -> left,
                                LinkedHashMap::new));
        List<RoadSign> dbSigns = roadSignRepository.findAll();
        Map<String, RoadSign> dbSignsByRoute = dbSigns.stream()
                .collect(Collectors.toMap(this::routeKeyFor, sign -> sign, (left, right) -> left));

        List<SignAuditItem> details = new ArrayList<>();
        int fullyConsistent = 0;
        int withIssues = 0;
        int orphanInDb = 0;
        int orphanInJson = 0;

        // Check every DB sign against JSON
        for (RoadSign dbSign : dbSigns) {
            String code = dbSign.getSignCode();
            CanonicalSignCatalogService.CanonicalSignSeed canonicalSign = canonicalSignsByRoute.get(routeKeyFor(dbSign));
            if (canonicalSign == null) {
                details.add(new SignAuditItem(code, "ORPHAN_IN_DB",
                        List.of("Sign exists in DB but not in signs_import")));
                orphanInDb++;
                withIssues++;
                continue;
            }
            List<String> issues = compareSign(dbSign, canonicalSign);
            if (issues.isEmpty()) {
                fullyConsistent++;
            } else {
                details.add(new SignAuditItem(code, "MISMATCH", issues));
                withIssues++;
            }
        }

        // Check for JSON signs not in DB
        for (CanonicalSignCatalogService.CanonicalSignSeed canonicalSign : canonicalSignsByRoute.values()) {
            if (!dbSignsByRoute.containsKey(canonicalSign.routeKey())) {
                details.add(new SignAuditItem(canonicalSign.routeCode(), "ORPHAN_IN_JSON",
                        List.of("Sign exists in signs_import but not in DB")));
                orphanInJson++;
                withIssues++;
            }
        }

        boolean passed = (withIssues == 0);
        log.info("Governance audit: {} DB signs, {} JSON signs, {} consistent, {} issues, passed={}",
                dbSigns.size(), canonicalSignsByRoute.size(), fullyConsistent, withIssues, passed);

        return new AuditResult(
                dbSigns.size(),
                canonicalSignsByRoute.size(),
                fullyConsistent,
                withIssues,
                orphanInDb,
                orphanInJson,
                passed,
                details);
    }

    /**
     * Compare every field persisted in road_signs against its canonical sign.json.
     */
    private List<String> compareSign(
            RoadSign dbSign,
            CanonicalSignCatalogService.CanonicalSignSeed canonicalSign) {
        List<String> issues = new ArrayList<>();

        compareField(issues, "sign_code", dbSign.getSignCode(), canonicalSign.routeCode());
        compareField(issues, "normalized_sign_code", dbSign.getNormalizedSignCode(), canonicalSign.routeKey());
        compareField(issues, "category", dbSign.getCategory() == null ? null : dbSign.getCategory().name(),
                canonicalSign.category().name());
        compareField(issues, "image_path", dbSign.getImagePath(), canonicalSign.imagePath());
        compareField(issues, "name_en", dbSign.getNameEn(), canonicalSign.nameEn());
        compareField(issues, "name_nl", dbSign.getNameNl(), canonicalSign.nameNl());
        compareField(issues, "name_fr", dbSign.getNameFr(), canonicalSign.nameFr());
        compareField(issues, "name_ar", dbSign.getNameAr(), canonicalSign.nameAr());
        compareField(issues, "description_en", dbSign.getDescriptionEn(), canonicalSign.descriptionEn());
        compareField(issues, "description_nl", dbSign.getDescriptionNl(), canonicalSign.descriptionNl());
        compareField(issues, "description_fr", dbSign.getDescriptionFr(), canonicalSign.descriptionFr());
        compareField(issues, "description_ar", dbSign.getDescriptionAr(), canonicalSign.descriptionAr());
        compareField(issues, "summary_en", dbSign.getSummaryEn(), canonicalSign.summaryEn());
        compareField(issues, "summary_nl", dbSign.getSummaryNl(), canonicalSign.summaryNl());
        compareField(issues, "summary_fr", dbSign.getSummaryFr(), canonicalSign.summaryFr());
        compareField(issues, "summary_ar", dbSign.getSummaryAr(), canonicalSign.summaryAr());
        compareField(
                issues, "driver_guidance_en", dbSign.getDriverGuidanceEn(), canonicalSign.driverGuidanceEn());
        compareField(
                issues, "driver_guidance_nl", dbSign.getDriverGuidanceNl(), canonicalSign.driverGuidanceNl());
        compareField(
                issues, "driver_guidance_fr", dbSign.getDriverGuidanceFr(), canonicalSign.driverGuidanceFr());
        compareField(
                issues, "driver_guidance_ar", dbSign.getDriverGuidanceAr(), canonicalSign.driverGuidanceAr());
        compareList(issues, "exceptions_en", dbSign.getExceptionsEn(), canonicalSign.exceptionsEn());
        compareList(issues, "exceptions_nl", dbSign.getExceptionsNl(), canonicalSign.exceptionsNl());
        compareList(issues, "exceptions_fr", dbSign.getExceptionsFr(), canonicalSign.exceptionsFr());
        compareList(issues, "exceptions_ar", dbSign.getExceptionsAr(), canonicalSign.exceptionsAr());
        if (!Objects.equals(Boolean.TRUE.equals(dbSign.getSeriousViolation()), canonicalSign.seriousViolation())) {
            issues.add("serious_violation: DB does not match sign.json");
        }
        if (!Boolean.TRUE.equals(dbSign.getIsActive())) {
            issues.add("is_active: DB sign is not active");
        }

        return issues;
    }

    private String routeKeyFor(RoadSign sign) {
        String normalized = RouteCodeNormalizer.normalize(sign.getNormalizedSignCode());
        return normalized.isBlank() ? RouteCodeNormalizer.normalize(sign.getSignCode()) : normalized;
    }

    private void compareField(List<String> issues, String field, String dbValue, String jsonValue) {
        if (jsonValue == null || jsonValue.isBlank()) {
            // JSON has no value — nothing to enforce
            return;
        }
        if (dbValue == null || dbValue.isBlank()) {
            issues.add(field + ": DB is null/empty but sign.json has a value");
            return;
        }
        if (!dbValue.equals(jsonValue)) {
            issues.add(field + ": DB does not match sign.json");
        }
    }

    private void compareList(List<String> issues, String field, List<String> dbValue, List<String> jsonValue) {
        if (dbValue == null || !dbValue.equals(jsonValue)) {
            issues.add(field + ": DB does not match sign.json");
        }
    }
}
