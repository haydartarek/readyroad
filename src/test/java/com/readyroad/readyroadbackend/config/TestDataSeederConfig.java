package com.readyroad.readyroadbackend.config;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

/**
 * Test Data Seeder Configuration
 *
 * Seeds H2 database with eligible exam questions for integration tests.
 *
 * Eligibility criteria:
 * - status = PUBLISHED
 * - isActive = true
 * - 2-3 options (Belgian standard)
 * - Required translations (NL/FR)
 *
 * Academic defense: This ensures a consistent test environment where
 * "Available: 0" failures are eliminated by guaranteeing 200+ eligible questions.
 */
@TestConfiguration
@Slf4j
public class TestDataSeederConfig {

    /**
     * Creates a bean that seeds test data when application context starts.
     * This bean is lazy-initialized but will run before any tests execute.
     */
    @Bean
    public TestDataSeeder testDataSeeder(
        CategoryRepository categoryRepo,
        QuizQuestionRepository questionRepo
    ) {
        return new TestDataSeeder(categoryRepo, questionRepo);
    }

    /**
     * Inner class that performs the actual seeding in constructor.
     */
    public static class TestDataSeeder {

        public TestDataSeeder(CategoryRepository categoryRepo, QuizQuestionRepository questionRepo) {
            seedData(categoryRepo, questionRepo);
        }

        private void seedData(CategoryRepository categoryRepo, QuizQuestionRepository questionRepo) {
            // Skip if already seeded
            if (questionRepo.count() > 0) {
                log.debug("Test data already seeded, skipping");
                return;
            }

            log.info("🌱 Seeding test database with exam-eligible questions...");

            // Create test categories
            Category trafficSigns = createCategory(categoryRepo, "SIGNS", "Traffic Signs",
                "Verkeersborden", "Panneaux de signalisation", "إشارات المرور");

            Category rules = createCategory(categoryRepo, "RULES", "Traffic Rules",
                "Verkeersregels", "Code de la route", "قواعد المرور");

            Category situations = createCategory(categoryRepo, "SITUATE", "Road Situations",
                "Verkeerssituaties", "Situations routières", "مواقف الطريق");

            // Create 200 PUBLISHED questions (supports 4 consecutive exams with 24h cooldown)
            // Belgian exam requires 50, we create 200 to support 4 exams
            int questionsPerCategory = 67;

            createQuestions(questionRepo, trafficSigns, 1, questionsPerCategory);
            createQuestions(questionRepo, rules, questionsPerCategory + 1, questionsPerCategory * 2);
            createQuestions(questionRepo, situations, questionsPerCategory * 2 + 1, 200);

            long total = questionRepo.count();
            log.info("✅ Test database seeded: {} published questions with 2-3 options", total);
        }

        private Category createCategory(CategoryRepository repo, String code, String nameEn,
                                        String nameNl, String nameFr, String nameAr) {
            Category category = new Category();
            category.setCode(code);
            category.setNameEn(nameEn);
            category.setNameNl(nameNl);
            category.setNameFr(nameFr);
            category.setNameAr(nameAr);
            category.setIsActive(true);
            category.setDisplayOrder(1);
            return repo.save(category);
        }

        private void createQuestions(QuizQuestionRepository repo, Category category,
                                     int startIdx, int endIdx) {
            for (int i = startIdx; i <= endIdx; i++) {
                QuizQuestion question = new QuizQuestion();

                // Required translations (NL/FR are mandatory for Belgian exam)
                question.setQuestionNl("NL: Wat is de correcte handeling " + i + "?");
                question.setQuestionFr("FR: Quelle est l'action correcte " + i + "?");
                question.setQuestionEn("EN: What is the correct action " + i + "?");
                question.setQuestionAr("AR: ما هو الإجراء الصحيح " + i + "؟");

                // Metadata
                question.setCategory(category);
                question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);

                // Difficulty distribution (realistic for adaptive testing)
                question.setDifficultyLevel(getDifficultyForIndex(i));

                // ✅ CRITICAL: Mark as PUBLISHED and ACTIVE (eligibility gates)
                question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
                question.setIsActive(true);

                // Belgian standard: 2-3 options
                question.setOptions(new ArrayList<>());

                // Option 1 (correct)
                QuizAnswerOption opt1 = new QuizAnswerOption();
                opt1.setOptionTextNl("NL Optie A");
                opt1.setOptionTextFr("FR Option A");
                opt1.setOptionTextEn("EN Option A");
                opt1.setOptionTextAr("AR خيار A");
                opt1.setIsCorrect(true);
                opt1.setDisplayOrder(1);
                opt1.setQuestion(question);
                question.getOptions().add(opt1);

                // Option 2 (incorrect)
                QuizAnswerOption opt2 = new QuizAnswerOption();
                opt2.setOptionTextNl("NL Optie B");
                opt2.setOptionTextFr("FR Option B");
                opt2.setOptionTextEn("EN Option B");
                opt2.setOptionTextAr("AR خيار B");
                opt2.setIsCorrect(false);
                opt2.setDisplayOrder(2);
                opt2.setQuestion(question);
                question.getOptions().add(opt2);

                // Option 3 (incorrect, added for ~50% of questions)
                if (i % 2 == 0) {
                    QuizAnswerOption opt3 = new QuizAnswerOption();
                    opt3.setOptionTextNl("NL Optie C");
                    opt3.setOptionTextFr("FR Option C");
                    opt3.setOptionTextEn("EN Option C");
                    opt3.setOptionTextAr("AR خيار C");
                    opt3.setIsCorrect(false);
                    opt3.setDisplayOrder(3);
                    opt3.setQuestion(question);
                    question.getOptions().add(opt3);
                }

                repo.save(question);
            }
        }

        /**
         * Distribute difficulty levels realistically:
         * - 30% EASY (questions 1-60)
         * - 50% MEDIUM (questions 61-160)
         * - 20% HARD (questions 161-200)
         */
        private QuizQuestion.DifficultyLevel getDifficultyForIndex(int i) {
            if (i <= 60) return QuizQuestion.DifficultyLevel.EASY;
            if (i <= 160) return QuizQuestion.DifficultyLevel.MEDIUM;
            return QuizQuestion.DifficultyLevel.HARD;
        }
    }
}
