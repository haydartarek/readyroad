package com.readyroad.readyroadbackend;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

/**
 * Base class for all integration tests.
 * Ensures the H2 database contains the published-question fixture required by
 * exam integration tests.
 */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public abstract class BaseIntegrationTest {

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected QuizQuestionRepository quizQuestionRepository;

    @Autowired(required = false)
    protected ExamSimulationRepository examSimulationRepository;

    @Autowired(required = false)
    protected ExamSimulationAnswerRepository examSimulationAnswerRepository;

    private static final Object SEED_LOCK = new Object();

    @BeforeEach
    public void ensureTestDataSeeded() {
        // ✅ ONLY clean up exams - KEEP questions
        if (examSimulationAnswerRepository != null) {
            examSimulationAnswerRepository.deleteAll();
        }
        if (examSimulationRepository != null) {
            examSimulationRepository.deleteAll();
        }

        // ✅ Check if already seeded - count published questions
        long publishedCount = quizQuestionRepository
                .findAll()
                .stream()
                .filter(q -> q.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED)
                .filter(QuizQuestion::getIsActive)
                .count();

        if (publishedCount >= 200) {
            log.debug("✓ Test data exists ({} published questions)", publishedCount);
            return;
        }

        // ✅ Seed database if needed
        synchronized (SEED_LOCK) {
            // Double-check after acquiring lock
            publishedCount = quizQuestionRepository
                    .findAll()
                    .stream()
                    .filter(q -> q.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED)
                    .filter(QuizQuestion::getIsActive)
                    .count();

            if (publishedCount >= 200) {
                return;
            }

            log.info("🌱 Seeding H2 database with 200 published questions...");

            // Create or reuse categories
            Category signs = getOrCreateCategory("SIGNS", "Traffic Signs");
            Category rules = getOrCreateCategory("RULES", "Traffic Rules");
            Category situations = getOrCreateCategory("SITUATE", "Road Situations");

            // Create 200 questions (67 + 67 + 66)
            createQuestions(signs, 1, 67);
            createQuestions(rules, 68, 134);
            createQuestions(situations, 135, 200);

            long finalCount = quizQuestionRepository.count();
            log.info("✅ Seeded {} questions successfully", finalCount);
        }
    }

    /**
     * Get existing category or create new one (prevents duplicate key violations)
     */
    protected Category getOrCreateCategory(String code, String name) {
        return categoryRepository.findByCode(code)
                .orElseGet(() -> {
                    log.debug("Creating category: {}", code);
                    Category cat = new Category();
                    cat.setCode(code);
                    cat.setNameEn(name);
                    cat.setNameNl(name + " NL");
                    cat.setNameFr(name + " FR");
                    cat.setNameAr(name + " AR");
                    cat.setIsActive(true);
                    cat.setDisplayOrder(1);
                    return categoryRepository.saveAndFlush(cat);
                });
    }

    /**
     * Create questions with eager-loaded options (prevents LazyInitializationException)
     */
    private void createQuestions(Category category, int start, int end) {
        for (int i = start; i <= end; i++) {
            QuizQuestion q = new QuizQuestion();
            q.setQuestionNl("NL Question " + i);
            q.setQuestionFr("FR Question " + i);
            q.setQuestionEn("EN Question " + i);
            q.setQuestionAr("AR Question " + i);
            q.setCategory(category);
            q.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);

            // Distribute difficulty: 30% EASY, 50% MEDIUM, 20% HARD
            if (i <= 60) {
                q.setDifficultyLevel(QuizQuestion.DifficultyLevel.EASY);
            } else if (i <= 160) {
                q.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
            } else {
                q.setDifficultyLevel(QuizQuestion.DifficultyLevel.HARD);
            }

            q.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
            q.setIsActive(true);
            q.setOptions(new ArrayList<>());

            // Add answer options (cascade persist)
            q.getOptions().add(createOption(q, "A", true, 1));
            q.getOptions().add(createOption(q, "B", false, 2));
            if (i % 2 == 0) {
                q.getOptions().add(createOption(q, "C", false, 3));
            }

            quizQuestionRepository.save(q);
        }
    }

    private QuizAnswerOption createOption(QuizQuestion q, String text, boolean correct, int order) {
        QuizAnswerOption opt = new QuizAnswerOption();
        opt.setQuestion(q);
        opt.setOptionTextNl("NL " + text);
        opt.setOptionTextFr("FR " + text);
        opt.setOptionTextEn("EN " + text);
        opt.setOptionTextAr("AR " + text);
        opt.setIsCorrect(correct);
        opt.setDisplayOrder(order);
        return opt;
    }
}
