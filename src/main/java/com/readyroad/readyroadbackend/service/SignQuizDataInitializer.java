package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Runs once on every application startup.
 *
 * Auto-import: if {@code road_signs} or {@code sign_questions} tables are
 * empty,
 * triggers a full import from {@code src/main/resources/data/signs_import/}.
 * Each sign has exactly one exam with 8 questions.
 */
@Slf4j
@Component
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

            boolean needsImport = signCount < expectedSignCount
                    || questionCount < expectedQuestionCount
                    || examCount < expectedExamCount;

            if (needsImport) {
                log.info("╔══════════════════════════════════════════════════════════════╗");
                log.info("║  Sign quiz data incomplete (signs={}/{}, questions={}/{}, exams={}/{}) — import ║",
                        signCount, expectedSignCount,
                        questionCount, expectedQuestionCount,
                        examCount, expectedExamCount);
                log.info("╚══════════════════════════════════════════════════════════════╝");

                if (signCount > 0 && questionCount == 0) {
                    log.info("Clearing {} stale road_signs rows before re-import", signCount);
                    jdbcTemplate.update("DELETE FROM road_signs");
                }

                var run = signQuizImportService.runImport("SYSTEM_INIT");

                log.info("Import finished — status={}, signs={}, questions={}, errors={}",
                        run.getStatus(), run.getSignsCreated(),
                        run.getQuestionsCreated(), run.getErrorsCount());
            } else {
                log.info("Sign quiz data already present ({} active signs, {} questions, {} exams) — nothing to do",
                        signCount, questionCount, examCount);
            }

        } catch (Exception e) {
            log.error("SignQuizDataInitializer failed (non-fatal): {}", e.getMessage(), e);
        }
    }
}
