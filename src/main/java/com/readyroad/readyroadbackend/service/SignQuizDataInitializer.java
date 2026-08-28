package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Runs once on every application startup.
 *
 * Reconciles the database with {@code signs_import} on every startup. The
 * importer performs in-place upserts, so content corrections are applied even
 * when the row counts already match the expected 184/1472/184 totals.
 */
@Slf4j
@Component
@Profile("!production-mirror")
@RequiredArgsConstructor
public class SignQuizDataInitializer {

    private static final int QUESTIONS_PER_SIGN = 8;

    private final RoadSignRepository roadSignRepository;
    private final SignQuizImportService signQuizImportService;
    private final CanonicalSignCatalogService canonicalSignCatalogService;
    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            long signCount = roadSignRepository.countByIsActiveTrue();
            long questionCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sign_questions", Long.class);
            long examCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sign_exams", Long.class);
            long expectedSignCount = canonicalSignCatalogService.getCanonicalSeeds().size();
            long expectedQuestionCount = expectedSignCount * QUESTIONS_PER_SIGN;
            long expectedExamCount = expectedSignCount;

            boolean incomplete = signCount < expectedSignCount
                    || questionCount < expectedQuestionCount
                    || examCount < expectedExamCount;

            if (incomplete) {
                log.info("╔══════════════════════════════════════════════════════════════╗");
                log.info("║  Sign quiz data incomplete (signs={}/{}, questions={}/{}, exams={}/{}) — import ║",
                        signCount, expectedSignCount,
                        questionCount, expectedQuestionCount,
                        examCount, expectedExamCount);
                log.info("╚══════════════════════════════════════════════════════════════╝");

            } else {
                log.info("Sign quiz data complete ({} active signs, {} questions, {} exams) — reconciling canonical content",
                        signCount, questionCount, examCount);
            }

            if (signCount > 0 && questionCount == 0) {
                log.info("Clearing {} stale road_signs rows before re-import", signCount);
                jdbcTemplate.update("DELETE FROM road_signs");
            }

            var run = signQuizImportService.runImport("SYSTEM_INIT");

            log.info("Import finished — status={}, signs={}, questions={}, errors={}",
                    run.getStatus(), run.getSignsCreated(),
                    run.getQuestionsCreated(), run.getErrorsCount());

        } catch (Exception e) {
            log.error("SignQuizDataInitializer failed (non-fatal): {}", e.getMessage(), e);
        }
    }
}
