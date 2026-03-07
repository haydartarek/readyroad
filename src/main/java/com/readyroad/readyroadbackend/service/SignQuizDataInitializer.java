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
 * <b>Phase 1 — Auto-import:</b>
 *   If {@code road_signs} table is empty the sign-quiz data has never been
 *   loaded.  We call {@link SignQuizImportService#runImport(String)} to
 *   populate all tables from {@code src/main/resources/data/signs_import/}.
 *
 * <b>Phase 2 — Exam-2 mirror:</b>
 *   After a fresh import each sign will have:
 *     • Exam 1 → questions  Q01-Q05  (5 real questions)
 *     • Exam 2 → 0 questions          (all entries in exams.json are placeholders)
 *   Because the full bank of 30 questions per sign (Q06-Q30) is not yet
 *   created, we keep the system functional by mirroring exam-1 into exam-2.
 *   This fill is idempotent: it only inserts into exam-2 rows that are missing.
 *
 * Once the full question bank is delivered the import data should be replaced
 * and this initializer will auto-reingest on the next clean-DB startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SignQuizDataInitializer {

    private final RoadSignRepository      roadSignRepository;
    private final SignQuizImportService   signQuizImportService;
    private final JdbcTemplate            jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            long signCount = roadSignRepository.countByIsActiveTrue();

            if (signCount == 0) {
                log.info("╔══════════════════════════════════════════════════╗");
                log.info("║  road_signs is empty — running sign quiz import  ║");
                log.info("╚══════════════════════════════════════════════════╝");

                var run = signQuizImportService.runImport("SYSTEM_INIT");

                log.info("Import finished — status={}, signs={}, questions={}, errors={}",
                        run.getStatus(), run.getSignsCreated(),
                        run.getQuestionsCreated(), run.getErrorsCount());

                mirrorExam1ToExam2();
            } else {
                log.info("Sign quiz data already present ({} active signs) — ensuring exam-2 is filled",
                        signCount);
                mirrorExam1ToExam2();
            }
        } catch (Exception e) {
            // Never crash startup — log and continue
            log.error("SignQuizDataInitializer failed (non-fatal): {}", e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Mirrors all questions from exam-1 into exam-2 <b>only</b> for signs whose
     * exam-2 is currently empty.  Safe to run multiple times.
     */
    private void mirrorExam1ToExam2() {
        String sql = """
            INSERT INTO sign_exam_questions (exam_id, question_id, question_order)
            SELECT
                e2.id          AS exam_id,
                seq.question_id,
                seq.question_order
            FROM sign_exams  e1
            JOIN sign_exams  e2  ON  e2.sign_id     = e1.sign_id
                                 AND e2.exam_number  = 2
                                 AND e2.is_active    = 1
            JOIN sign_exam_questions seq ON seq.exam_id = e1.id
            WHERE e1.exam_number = 1
              AND e1.is_active   = 1
              AND NOT EXISTS (
                  SELECT 1
                  FROM sign_exam_questions g
                  WHERE g.exam_id = e2.id
                  LIMIT 1
              )
            """;

        int rows = jdbcTemplate.update(sql);
        if (rows > 0) {
            log.info("Exam-2 mirror: inserted {} question-links into exam-2 rows", rows);
        } else {
            log.info("Exam-2 mirror: all exam-2 rows already contain questions — nothing to do");
        }
    }
}
