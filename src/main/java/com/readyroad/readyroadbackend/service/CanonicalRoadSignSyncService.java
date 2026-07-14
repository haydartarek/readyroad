package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.util.RouteCodeNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class CanonicalRoadSignSyncService {

    private static final Logger log = LoggerFactory.getLogger(CanonicalRoadSignSyncService.class);

    private final RoadSignRepository roadSignRepository;
    private final CanonicalSignCatalogService canonicalSignCatalogService;

    public CanonicalRoadSignSyncService(
            RoadSignRepository roadSignRepository,
            CanonicalSignCatalogService canonicalSignCatalogService) {
        this.roadSignRepository = roadSignRepository;
        this.canonicalSignCatalogService = canonicalSignCatalogService;
    }

    @Transactional
    public int syncCanonicalFields() {
        List<RoadSign> existingSigns = roadSignRepository.findAll();
        Map<String, RoadSign> byRoute = new LinkedHashMap<>();
        Map<String, RoadSign> bySignCode = new LinkedHashMap<>();
        for (RoadSign sign : existingSigns) {
            String routeKey = normalizeRouteKey(sign.getNormalizedSignCode());
            if (!routeKey.isBlank()) {
                byRoute.putIfAbsent(routeKey, sign);
            }

            String signCode = normalizeCode(sign.getSignCode());
            if (!signCode.isBlank()) {
                bySignCode.putIfAbsent(signCode, sign);
            }
        }

        List<RoadSign> toSave = new ArrayList<>();
        List<RoadSign> toDelete = new ArrayList<>();
        Set<Long> claimedIds = new LinkedHashSet<>();
        int created = 0;
        int updated = 0;
        int reactivated = 0;
        int deleted = 0;

        for (CanonicalSignCatalogService.CanonicalSignSeed seed : canonicalSignCatalogService.getCanonicalSeeds()) {
            RoadSign sign = findMatchingSign(seed, byRoute, bySignCode, claimedIds);

            boolean isNew = false;
            if (sign == null) {
                sign = new RoadSign();
                isNew = true;
            }

            boolean wasInactive = !Boolean.TRUE.equals(sign.getIsActive());
            boolean changed = applySeed(sign, seed);
            if (isNew) {
                created++;
                toSave.add(sign);
                byRoute.put(seed.routeKey(), sign);
                bySignCode.put(normalizeCode(seed.routeCode()), sign);
                continue;
            }

            if (sign.getId() != null) {
                claimedIds.add(sign.getId());
            }

            if (changed) {
                if (wasInactive) {
                    reactivated++;
                } else {
                    updated++;
                }
                toSave.add(sign);
            }

            byRoute.put(seed.routeKey(), sign);
            bySignCode.put(normalizeCode(seed.routeCode()), sign);
        }

        for (RoadSign sign : existingSigns) {
            if (sign.getId() != null && !claimedIds.contains(sign.getId())) {
                toDelete.add(sign);
                deleted++;
            }
        }

        if (!toSave.isEmpty()) {
            roadSignRepository.saveAll(toSave);
        }

        if (!toDelete.isEmpty()) {
            roadSignRepository.deleteAll(toDelete);
        }

        int changedRows = created + updated + reactivated + deleted;
        log.info(
                "Canonical road-sign sync completed: created={}, updated={}, reactivated={}, deleted={}, totalChanged={}",
                created,
                updated,
                reactivated,
                deleted,
                changedRows);
        return changedRows;
    }

    private RoadSign findMatchingSign(
            CanonicalSignCatalogService.CanonicalSignSeed seed,
            Map<String, RoadSign> byRoute,
            Map<String, RoadSign> bySignCode,
            Set<Long> claimedIds) {
        RoadSign byRouteMatch = byRoute.get(seed.routeKey());
        if (isAlreadyClaimed(byRouteMatch, claimedIds)) {
            byRouteMatch = null;
        }
        if (byRouteMatch != null) {
            return byRouteMatch;
        }

        if (canonicalSignCatalogService.hasMultipleVariants(seed.signCode())) {
            return null;
        }

        RoadSign byCodeMatch = bySignCode.get(normalizeCode(seed.signCode()));
        if (isAlreadyClaimed(byCodeMatch, claimedIds)) {
            return null;
        }
        if (byCodeMatch == null) {
            return null;
        }

        String existingRouteKey = normalizeRouteKey(byCodeMatch.getNormalizedSignCode());
        if (!existingRouteKey.isBlank() && !existingRouteKey.equals(seed.routeKey())) {
            log.warn(
                    "Skipping conflicting canonical sign reuse for code {}: existing routeKey={} target routeKey={}",
                    seed.signCode(),
                    existingRouteKey,
                    seed.routeKey());
            return null;
        }

        return byCodeMatch;
    }

    private boolean applySeed(RoadSign sign, CanonicalSignCatalogService.CanonicalSignSeed seed) {
        boolean changed = false;
        changed |= setIfDifferent(sign.getSignCode(), seed.routeCode(), sign::setSignCode);
        changed |= setIfDifferent(sign.getNormalizedSignCode(), seed.routeKey(), sign::setNormalizedSignCode);
        changed |= setIfDifferent(sign.getCategory(), seed.category(), sign::setCategory);
        changed |= setIfDifferent(sign.getImagePath(), seed.imagePath(), sign::setImagePath);
        changed |= setIfDifferent(sign.getNameEn(), seed.nameEn(), sign::setNameEn);
        changed |= setIfDifferent(sign.getNameAr(), seed.nameAr(), sign::setNameAr);
        changed |= setIfDifferent(sign.getNameNl(), seed.nameNl(), sign::setNameNl);
        changed |= setIfDifferent(sign.getNameFr(), seed.nameFr(), sign::setNameFr);
        changed |= setIfDifferent(sign.getDescriptionEn(), seed.descriptionEn(), sign::setDescriptionEn);
        changed |= setIfDifferent(sign.getDescriptionAr(), seed.descriptionAr(), sign::setDescriptionAr);
        changed |= setIfDifferent(sign.getDescriptionNl(), seed.descriptionNl(), sign::setDescriptionNl);
        changed |= setIfDifferent(sign.getDescriptionFr(), seed.descriptionFr(), sign::setDescriptionFr);
        changed |= setIfDifferent(sign.getSummaryEn(), seed.summaryEn(), sign::setSummaryEn);
        changed |= setIfDifferent(sign.getSummaryAr(), seed.summaryAr(), sign::setSummaryAr);
        changed |= setIfDifferent(sign.getSummaryNl(), seed.summaryNl(), sign::setSummaryNl);
        changed |= setIfDifferent(sign.getSummaryFr(), seed.summaryFr(), sign::setSummaryFr);
        changed |= setIfDifferent(
                sign.getDriverGuidanceEn(), seed.driverGuidanceEn(), sign::setDriverGuidanceEn);
        changed |= setIfDifferent(
                sign.getDriverGuidanceAr(), seed.driverGuidanceAr(), sign::setDriverGuidanceAr);
        changed |= setIfDifferent(
                sign.getDriverGuidanceNl(), seed.driverGuidanceNl(), sign::setDriverGuidanceNl);
        changed |= setIfDifferent(
                sign.getDriverGuidanceFr(), seed.driverGuidanceFr(), sign::setDriverGuidanceFr);
        changed |= setIfDifferent(sign.getExceptionsEn(), seed.exceptionsEn(), sign::setExceptionsEn);
        changed |= setIfDifferent(sign.getExceptionsAr(), seed.exceptionsAr(), sign::setExceptionsAr);
        changed |= setIfDifferent(sign.getExceptionsNl(), seed.exceptionsNl(), sign::setExceptionsNl);
        changed |= setIfDifferent(sign.getExceptionsFr(), seed.exceptionsFr(), sign::setExceptionsFr);
        changed |= setIfDifferent(sign.getSeriousViolation(), seed.seriousViolation(), sign::setSeriousViolation);

        if (!Boolean.TRUE.equals(sign.getIsActive())) {
            sign.setIsActive(true);
            changed = true;
        }

        return changed;
    }

    private static String normalizeRouteKey(String value) {
        return RouteCodeNormalizer.normalize(value);
    }

    private static String normalizeCode(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isAlreadyClaimed(RoadSign sign, Set<Long> claimedIds) {
        return sign != null && sign.getId() != null && claimedIds.contains(sign.getId());
    }

    private static <T> boolean setIfDifferent(T current, T next, java.util.function.Consumer<T> setter) {
        if (current == null ? next == null : current.equals(next)) {
            return false;
        }
        setter.accept(next);
        return true;
    }
}
