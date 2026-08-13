package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.entity.SignChoice;
import com.readyroad.readyroadbackend.domain.entity.SignExam;
import com.readyroad.readyroadbackend.domain.entity.SignExamQuestion;
import com.readyroad.readyroadbackend.domain.entity.SignExamResult;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.SignRandomPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.SignQuestion;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.entity.Notification;
import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamResultRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamRepository;
import com.readyroad.readyroadbackend.domain.repository.SignPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignRandomPracticeQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignRandomPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserWeakAreaRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.sign.SignExamQuestionsDto;
import com.readyroad.readyroadbackend.dto.sign.SignExamResultDto;
import com.readyroad.readyroadbackend.dto.sign.SignExamAnswerItem;
import com.readyroad.readyroadbackend.dto.sign.SignExamHistoryResponseDto;
import com.readyroad.readyroadbackend.dto.sign.SignPracticeSessionDto;
import com.readyroad.readyroadbackend.dto.sign.SignQuizQuestionDto;
import com.readyroad.readyroadbackend.dto.sign.SignRandomPracticeAnswerRequest;
import com.readyroad.readyroadbackend.dto.sign.SignRandomPracticeResultDto;
import com.readyroad.readyroadbackend.dto.sign.SignRandomPracticeSessionDto;
import com.readyroad.readyroadbackend.dto.sign.SignUserProgressDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SignQuizServiceIntegrationTest {

        @SuppressWarnings("unused") // reserved for future sign-code validation tests
        private static final Pattern RAW_SIGN_CODE_PATTERN = Pattern.compile(
                        "(?<![A-Za-z0-9_-])[A-Z]{1,4}\\d{1,3}(?:[A-Za-z]+|[_-][A-Za-z0-9]+)*(?![A-Za-z0-9_-])");

        @Autowired
        private SignQuizService signQuizService;

        @Autowired
        private RoadSignRepository roadSignRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private SignExamResultRepository signExamResultRepository;

        @Autowired
        private SignExamRepository signExamRepository;

        @Autowired
        private SignPracticeSessionRepository signPracticeSessionRepository;

        @Autowired
        private SignQuestionRepository signQuestionRepository;

        @Autowired
        private EntityManager entityManager;

        @Autowired
        private EntityManagerFactory entityManagerFactory;

        @Autowired
        private SignRandomPracticeSessionRepository signRandomPracticeSessionRepository;

        @Autowired
        private SignRandomPracticeQuestionRepository signRandomPracticeQuestionRepository;

        @Autowired
        private NotificationRepository notificationRepository;

        @Autowired
        private UserWeakAreaRepository userWeakAreaRepository;

        @Autowired
        private CanonicalSignCatalogService canonicalSignCatalogService;

        @Test
        @DisplayName("Submitting an empty sign exam returns a failed unanswered result and stores the attempt")
        void submitExamAllowsEmptyAnswers() {
                User user = createUser();
                RoadSign sign = createSignWithSingleQuestionExam();

                SignExamResultDto result = signQuizService.submitExam(
                                sign.getSignCode(),
                                1,
                                List.of(),
                                user.getId());

                assertThat(result.signCode()).isEqualTo(sign.getSignCode());
                assertThat(result.examNumber()).isEqualTo(1);
                assertThat(result.totalLinked()).isEqualTo(1);
                assertThat(result.answeredCount()).isZero();
                assertThat(result.unansweredCount()).isEqualTo(1);
                assertThat(result.correctAnswers()).isZero();
                assertThat(result.wrongAnswers()).isZero();
                assertThat(result.scorePercentage()).isZero();
                assertThat(result.passingThreshold()).isEqualTo(1);
                assertThat(result.passed()).isFalse();
                assertThat(result.resultStatus()).isEqualTo("FAILED");
                assertThat(result.questionResults())
                                .singleElement()
                                .satisfies(questionResult -> {
                                        assertThat(questionResult.answered()).isFalse();
                                        assertThat(questionResult.isCorrect()).isNull();
                                        assertThat(questionResult.selectedChoiceId()).isNull();
                                });

                assertThat(signExamResultRepository.findByUserIdAndSignCodeOrderByCompletedAtDesc(
                                user.getId(),
                                sign.getSignCode()))
                                .singleElement()
                                .satisfies(savedAttempt -> assertSavedAttempt(savedAttempt, user.getId(), sign.getId(),
                                                sign.getSignCode()));
        }

        @Test
        @DisplayName("Submitting a sign exam stores review details, creates a notification, and exposes history")
        void submitExamCreatesNotificationAndHistory() {
                User user = createUser("sign-exam-history");
                RoadSign sign = createSignWithSingleQuestionExam();
                SignQuestion question = signQuestionRepository.findAllBySignIdAndIsActiveTrue(sign.getId()).get(0);
                Long correctChoiceId = question.getChoices().stream()
                                .filter(choice -> Boolean.TRUE.equals(choice.getIsCorrect()))
                                .findFirst()
                                .map(SignChoice::getId)
                                .orElseThrow();

                SignExamResultDto result = signQuizService.submitExam(
                                sign.getSignCode(),
                                1,
                                List.of(new SignExamAnswerItem(question.getId(), correctChoiceId)),
                                user.getId());

                assertThat(result.resultId()).isNotNull();
                assertThat(result.passed()).isTrue();
                assertThat(result.questionResults()).singleElement()
                                .satisfies(item -> {
                                        assertThat(item.selectedChoiceId()).isEqualTo(correctChoiceId);
                                        assertThat(item.selectedTextEn()).isEqualTo("Correct");
                                        assertThat(item.selectedTextAr()).isEqualTo("صحيح");
                                });

                SignExamHistoryResponseDto history = signQuizService.getSignExamHistory(user.getId());
                assertThat(history.totalResults()).isEqualTo(1);
                assertThat(history.results()).singleElement().satisfies(item -> {
                        assertThat(item.resultId()).isEqualTo(result.resultId());
                        assertThat(item.signCode()).isEqualTo(sign.getSignCode());
                        assertThat(item.passed()).isTrue();
                });

                SignExamResultDto stored = signQuizService.getStoredSignExamResult(result.resultId(), user.getId());
                assertThat(stored.resultId()).isEqualTo(result.resultId());
                assertThat(stored.questionResults()).singleElement()
                                .satisfies(item -> {
                                        assertThat(item.selectedChoiceId()).isEqualTo(correctChoiceId);
                                        assertThat(item.selectedTextEn()).isEqualTo("Correct");
                                        assertThat(item.selectedTextAr()).isEqualTo("صحيح");
                                });

                assertThat(notificationRepository.findTop50ByUserIdOrderByCreatedAtDesc(user.getId()))
                                .singleElement()
                                .satisfies(notification -> {
                                        assertThat(notification.getType()).isEqualTo(NotificationType.EXAM_PASSED);
                                        assertThat(notification.getMessageKey())
                                                        .isEqualTo("notif.msg.sign_exam_passed");
                                        assertThat(notification.getLink())
                                                        .isEqualTo("/dashboard?section=exam-results&signExamResultId="
                                                                        + result.resultId());
                                });
        }

        @Test
        @DisplayName("Starting a mixed sign exam builds 50 questions with 20 easy, 20 medium, 10 hard and resumes the same session")
        void startRandomSignPracticeSessionBuildsExpectedDistributionAndResumesActiveSession() {
                seedRandomPracticePool();
                User user = createUser("sign-random-start");

                SignRandomPracticeSessionDto started = signQuizService.startRandomSignPracticeSession(user.getId());

                assertThat(started.status()).isEqualTo(SignRandomPracticeSession.SessionStatus.IN_PROGRESS.name());
                assertThat(started.totalQuestions()).isEqualTo(50);
                assertThat(started.passingScore()).isEqualTo(41);
                assertThat(started.questions()).hasSize(50);
                assertDifficultyDistribution(started.questions());

                SignRandomPracticeSessionDto resumed = signQuizService.startRandomSignPracticeSession(user.getId());

                assertThat(resumed.sessionId()).isEqualTo(started.sessionId());
                assertThat(extractQuestionIds(resumed.questions()))
                                .containsExactlyElementsOf(extractQuestionIds(started.questions()));
                assertThat(signRandomPracticeQuestionRepository
                                .findBySessionIdOrderByQuestionOrder(started.sessionId()))
                                .hasSize(50);
        }

        @Test
        @DisplayName("Repeating a sign exam submission key returns the same historical result")
        void submitExamIsIdempotentForTheSameSubmissionKey() {
                User user = createUser("sign-exam-idempotent");
                RoadSign sign = createSignWithSingleQuestionExam();

                SignExamResultDto first = signQuizService.submitExam(
                                sign.getSignCode(), 1, List.of(), user.getId(), "attempt-one");
                SignExamResultDto second = signQuizService.submitExam(
                                sign.getSignCode(), 1, List.of(), user.getId(), "attempt-one");

                assertThat(second.resultId()).isEqualTo(first.resultId());
                assertThat(signExamResultRepository.findByUserIdAndSignCodeOrderByCompletedAtDesc(
                                user.getId(), sign.getSignCode())).hasSize(1);
        }

        @Test
        @DisplayName("Random practice candidates eagerly load the sign and choices used by eligibility filtering")
        void randomPracticeCandidateQueryLoadsFilteringDependencies() {
                seedRandomPracticePool();
                entityManager.flush();
                entityManager.clear();

                List<SignQuestion> candidates = signQuestionRepository
                                .findAllActiveForActiveSignsByDifficulty(SignDifficulty.EASY);

                assertThat(candidates).isNotEmpty();
                assertThat(candidates)
                                .allSatisfy(question -> {
                                        assertThat(entityManagerFactory.getPersistenceUnitUtil()
                                                        .isLoaded(question, "sign")).isTrue();
                                        assertThat(entityManagerFactory.getPersistenceUnitUtil()
                                                        .isLoaded(question, "choices")).isTrue();
                                });

                entityManager.clear();
                assertThat(candidates)
                                .allSatisfy(question -> {
                                        assertThat(question.getSign().getSignCode()).isNotBlank();
                                        assertThat(question.getDeliverableChoices()).isNotEmpty();
                                });
        }

        @Test
        @DisplayName("All-sign progress preserves single-sign semantics with a bounded query count")
        void allUserProgressPreservesSemanticsWithBoundedQueryCount() {
                List<CanonicalSignCatalogService.CanonicalSignSeed> seeds = canonicalSignCatalogService
                                .getCanonicalSeeds();
                assertThat(seeds).hasSize(184);

                List<RoadSign> signs = seeds.stream()
                                .map(this::ensureCanonicalSignWithExam)
                                .toList();
                User user = createUser("sign-progress-performance");
                RoadSign targetSign = signs.get(0);

                SignPracticeSession inProgress = createPracticeSession(
                                user,
                                targetSign,
                                SignPracticeSession.SessionStatus.IN_PROGRESS,
                                8,
                                3);
                SignPracticeSession completed = createPracticeSession(
                                user,
                                targetSign,
                                SignPracticeSession.SessionStatus.COMPLETED,
                                8,
                                5);
                signPracticeSessionRepository.saveAllAndFlush(List.of(inProgress, completed));

                SignExamResult failedAttempt = createSignExamResult(user, targetSign, 50.0, false);
                SignExamResult passedAttempt = createSignExamResult(user, targetSign, 75.0, true);
                signExamResultRepository.saveAllAndFlush(List.of(failedAttempt, passedAttempt));

                entityManager.clear();
                SignUserProgressDto expected = signQuizService.getUserSignProgress(
                                targetSign.getSignCode(),
                                user.getId());

                entityManager.clear();
                Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
                boolean statisticsWereEnabled = statistics.isStatisticsEnabled();
                statistics.setStatisticsEnabled(true);
                statistics.clear();

                try {
                        List<SignUserProgressDto> progress = signQuizService.getAllUserProgress(user.getId());

                        assertThat(progress).hasSize(184);
                        assertThat(progress)
                                        .filteredOn(item -> item.signCode().equals(targetSign.getSignCode()))
                                        .singleElement()
                                        .isEqualTo(expected);
                        assertThat(statistics.getPrepareStatementCount())
                                        .as("all-sign progress SQL statement count")
                                        .isEqualTo(4);
                } finally {
                        statistics.setStatisticsEnabled(statisticsWereEnabled);
                }
        }

        @Test
        @DisplayName("Abandoning individual sign practice is idempotent and excluded from learner history")
        void abandonSignPracticeIsExcludedFromHistory() {
                RoadSign sign = ensureCanonicalSignWithExam(
                                canonicalSignCatalogService.getCanonicalSeeds().get(0));
                User user = createUser("sign-practice-abandon");
                SignPracticeSession session = createPracticeSession(
                                user, sign, SignPracticeSession.SessionStatus.IN_PROGRESS, 8, 0);
                session = signPracticeSessionRepository.saveAndFlush(session);

                signQuizService.abandonPracticeSession(session.getId(), user.getId());
                signQuizService.abandonPracticeSession(session.getId(), user.getId());

                assertThat(signPracticeSessionRepository.findById(session.getId()))
                                .hasValueSatisfying(stored -> {
                                        assertThat(stored.getStatus())
                                                        .isEqualTo(SignPracticeSession.SessionStatus.ABANDONED);
                                        assertThat(stored.getCompletedAt()).isNull();
                                });
                assertThat(signQuizService.getPracticeHistory(user.getId()).sessions()).isEmpty();
        }

        @Test
        @DisplayName("Random sign exam review preserves correct and wrong selected answers in every language")
        void randomSignExamReviewReturnsPersistedSelectedAnswers() {
                seedRandomPracticePool();
                User user = createUser("sign-random-review");
                SignRandomPracticeSessionDto session = signQuizService.startRandomSignPracticeSession(user.getId());
                SignQuizQuestionDto correctQuestion = session.questions().get(0);
                SignQuizQuestionDto wrongQuestion = session.questions().get(1);
                SignQuestion storedCorrectQuestion = signQuestionRepository.findById(correctQuestion.id()).orElseThrow();
                SignQuestion storedWrongQuestion = signQuestionRepository.findById(wrongQuestion.id()).orElseThrow();
                storedWrongQuestion.getSign().setSignCode("TEST-LONG-CODE-01");
                storedWrongQuestion.getSign().setNormalizedSignCode("test-long-code-01");
                roadSignRepository.saveAndFlush(storedWrongQuestion.getSign());
                assertThat(storedWrongQuestion.getSign().getSignCode()).hasSizeGreaterThan(10);
                Long correctChoiceId = storedCorrectQuestion.getDeliverableChoices().stream()
                                .filter(choice -> Boolean.TRUE.equals(choice.getIsCorrect()))
                                .findFirst()
                                .orElseThrow()
                                .getId();
                Long wrongChoiceId = storedWrongQuestion.getDeliverableChoices().stream()
                                .filter(choice -> !Boolean.TRUE.equals(choice.getIsCorrect()))
                                .findFirst()
                                .orElseThrow()
                                .getId();
                Map<Long, Long> correctChoiceByQuestion = session.questions().stream()
                                .collect(Collectors.toMap(
                                                SignQuizQuestionDto::id,
                                                question -> signQuestionRepository.findById(question.id())
                                                                .orElseThrow()
                                                                .getDeliverableChoices().stream()
                                                                .filter(choice -> Boolean.TRUE.equals(choice.getIsCorrect()))
                                                                .findFirst()
                                                                .orElseThrow()
                                                                .getId()));

                List<SignRandomPracticeAnswerRequest> answers = session.questions().stream()
                                .map(question -> new SignRandomPracticeAnswerRequest(
                                                question.id(),
                                                question.id().equals(correctQuestion.id())
                                                                ? correctChoiceId
                                                                : question.id().equals(wrongQuestion.id())
                                                                                ? wrongChoiceId
                                                                                : correctChoiceByQuestion.get(question.id())))
                                .toList();

                SignRandomPracticeResultDto submitted = signQuizService.submitRandomSignPracticeAnswers(
                                session.sessionId(), answers, user.getId());
                SignRandomPracticeResultDto.QuestionResult correctResult = submitted.questions().stream()
                                .filter(question -> question.questionId().equals(correctQuestion.id()))
                                .findFirst()
                                .orElseThrow();
                SignRandomPracticeResultDto.QuestionResult wrongResult = submitted.questions().stream()
                                .filter(question -> question.questionId().equals(wrongQuestion.id()))
                                .findFirst()
                                .orElseThrow();

                assertThat(correctResult.selectedChoiceId()).isEqualTo(correctChoiceId);
                assertThat(correctResult.selectedChoiceNl()).isNotBlank();
                assertThat(correctResult.selectedChoiceEn()).isNotBlank();
                assertThat(correctResult.selectedChoiceFr()).isNotBlank();
                assertThat(correctResult.selectedChoiceAr()).isNotBlank();
                assertThat(correctResult.isCorrect()).isTrue();
                assertThat(wrongResult.selectedChoiceId()).isEqualTo(wrongChoiceId);
                assertThat(wrongResult.selectedChoiceNl()).isNotBlank();
                assertThat(wrongResult.selectedChoiceEn()).isNotBlank();
                assertThat(wrongResult.selectedChoiceFr()).isNotBlank();
                assertThat(wrongResult.selectedChoiceAr()).isNotBlank();
                assertThat(wrongResult.isCorrect()).isFalse();
                assertThat(submitted.correctAnswers()).isEqualTo(49);
                assertThat(submitted.wrongAnswers()).isEqualTo(1);
                assertThat(submitted.unanswered()).isZero();

                entityManager.flush();
                entityManager.clear();

                SignRandomPracticeResultDto reloaded = signQuizService.getRandomSignPracticeResult(
                                session.sessionId(), user.getId());
                assertThat(reloaded.questions())
                                .filteredOn(question -> question.questionId().equals(wrongQuestion.id()))
                                .singleElement()
                                .satisfies(question -> {
                                        assertThat(question.selectedChoiceId()).isEqualTo(wrongChoiceId);
                                        assertThat(question.selectedChoiceEn()).isNotBlank();
                                        assertThat(question.correctChoiceEn()).isNotBlank();
                                        assertThat(question.selectedChoiceId()).isNotEqualTo(question.correctChoiceId());
                                        assertThat(question.isCorrect()).isFalse();
                                });
        }

        @Test
        @DisplayName("Incomplete mixed sign exam is abandoned without results or analytics and keeps the cooldown")
        void incompleteRandomSignPracticeIsAbandonedWithoutResult() {
                seedRandomPracticePool();
                User user = createUser("sign-random-submit");

                SignRandomPracticeSessionDto firstSession = signQuizService
                                .startRandomSignPracticeSession(user.getId());
                List<Long> firstQuestionIds = extractQuestionIds(firstSession.questions());

                List<SignRandomPracticeAnswerRequest> unansweredPayload = firstSession.questions().stream()
                                .map(question -> new SignRandomPracticeAnswerRequest(question.id(), null))
                                .toList();

                assertThatThrownBy(() -> signQuizService.submitRandomSignPracticeAnswers(
                                firstSession.sessionId(), unansweredPayload, user.getId()))
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("400 BAD_REQUEST");

                signQuizService.abandonRandomSignPracticeSession(firstSession.sessionId(), user.getId());
                signQuizService.abandonRandomSignPracticeSession(firstSession.sessionId(), user.getId());

                assertThat(signRandomPracticeSessionRepository.findById(firstSession.sessionId()))
                                .hasValueSatisfying(session -> {
                                        assertThat(session.getStatus())
                                                        .isEqualTo(SignRandomPracticeSession.SessionStatus.ABANDONED);
                                        assertThat(session.getAnsweredCount()).isZero();
                                        assertThat(session.getCorrectCount()).isZero();
                                        assertThat(session.getScorePct()).isNull();
                                        assertThat(session.getPassed()).isNull();
                                        assertThat(session.getCompletedAt()).isNull();
                                });
                assertThat(userWeakAreaRepository.findAllByUserId(user.getId())).isEmpty();
                assertThat(notificationRepository.findTop50ByUserIdOrderByCreatedAtDesc(user.getId())).isEmpty();
                assertThat(signQuizService.getRandomSignPracticeHistory(user.getId()).sessions()).isEmpty();

                SignRandomPracticeSessionDto secondSession = signQuizService
                                .startRandomSignPracticeSession(user.getId());
                List<Long> secondQuestionIds = extractQuestionIds(secondSession.questions());

                assertThat(new HashSet<>(firstQuestionIds))
                                .doesNotContainAnyElementsOf(secondQuestionIds);
                assertDifficultyDistribution(secondSession.questions());
        }

        @Test
        @DisplayName("Practice and sign exam delivery replace referenced sign codes with localized names")
        void practiceAndExamDeliveryReplaceReferencedSignCodes() {
                User user = createUser("sign-ref-delivery");
                ensureReferenceSignExists("B11", SignCategory.PRIORITY, "End of priority road",
                                "نهاية طريق الأولوية", "Fin de route prioritaire", "Einde van de voorrangsweg");
                ensureReferenceSignExists("B17", SignCategory.PRIORITY,
                                "Crossroads where priority from the right applies",
                                "تقاطع تسري فيه أولوية اليمين",
                                "Carrefour où la priorité de droite s'applique",
                                "Kruispunt waar voorrang van rechts geldt");

                CanonicalSignCatalogService.CanonicalSignSeed hostSeed = canonicalSignCatalogService.getCanonicalSeeds()
                                .stream()
                                .filter(seed -> !seed.routeCode().equals("B11"))
                                .filter(seed -> !seed.routeCode().equals("B17"))
                                .findFirst()
                                .orElseThrow();
                RoadSign sign = roadSignRepository.findByNormalizedSignCode(hostSeed.routeKey())
                                .orElseGet(() -> createCanonicalSign(hostSeed));

                SignQuestion question = new SignQuestion();
                question.setSign(sign);
                question.setQuestionRef(sign.getSignCode() + "_REFERENCE_RESOLUTION_Q01");
                question.setQuestionType(SignQuestionType.WHAT_MUST_YOU_DO);
                question.setDifficulty(SignDifficulty.EASY);
                question.setIsCritical(false);
                question.setShowSign(true);
                question.setQuestionNl(
                                "Na bord B11 nadert u een kruispunt met bord B17. Behoudt u daar nog voorrang op verkeer van rechts?");
                question.setQuestionEn(
                                "After sign B11 you approach a junction with sign B17. Do you still have priority over traffic from the right?");
                question.setQuestionFr(
                                "Après le panneau B11 vous approchez d'un carrefour avec le panneau B17. Conservez-vous encore la priorité sur le trafic venant de droite ?");
                question.setQuestionAr(
                                "بعد العلامة B11 تصل إلى تقاطع يحمل العلامة B17. هل تبقى لك الأولوية على حركة المرور القادمة من اليمين؟");
                question.setExplanationNl(
                                "B11 beëindigt de voorrangsweg. Bord B17 kondigt aan dat voorrang van rechts weer geldt.");
                question.setExplanationEn(
                                "B11 ends the priority road. Sign B17 announces that priority from the right applies again.");
                question.setExplanationFr(
                                "B11 met fin à la route prioritaire. Le panneau B17 annonce que la priorité de droite s'applique à nouveau.");
                question.setExplanationAr(
                                "تعني B11 نهاية طريق الأولوية، بينما تعني B17 أن أولوية اليمين تعود للتطبيق من جديد.");
                question.setIsActive(true);
                question.addChoice(createChoice(
                                "Ja, want B11 geeft u overal verder voorrang",
                                "Yes, because B11 keeps giving you priority everywhere ahead",
                                "Oui, car B11 vous donne encore la priorité partout plus loin",
                                "نعم، لأن B11 يبقي لك الأولوية في كل ما بعده",
                                false,
                                1));
                question.addChoice(createChoice(
                                "Nee, want B17 betekent dat voorrang van rechts opnieuw geldt",
                                "No, because B17 means priority from the right applies again",
                                "Non, car B17 signifie que la priorité de droite s'applique de nouveau",
                                "لا، لأن B17 تعني أن أولوية اليمين تسري من جديد",
                                true,
                                2));
                question.addChoice(createChoice(
                                "Alleen als B11 en B17 samen onder dezelfde verkeerslichten hangen",
                                "Only if B11 and B17 are mounted below the same traffic lights",
                                "Seulement si B11 et B17 sont placés sous les mêmes feux",
                                "فقط إذا كانت B11 وB17 تحت نفس الإشارة الضوئية",
                                false,
                                3));
                sign.getQuestions().add(question);

                SignExam exam = new SignExam();
                exam.setSign(sign);
                exam.setExamNumber(999);
                exam.setPassingScore(1);
                exam.setTotalQuestions(1);
                exam.setEasyCount(1);
                exam.setMediumCount(0);
                exam.setHardCount(0);
                exam.setIsActive(true);

                SignExamQuestion examQuestion = new SignExamQuestion();
                examQuestion.setQuestion(question);
                examQuestion.setQuestionOrder(1);
                exam.addExamQuestion(examQuestion);
                sign.getExams().add(exam);

                RoadSign savedSign = roadSignRepository.saveAndFlush(sign);

                SignPracticeSessionDto practiceSession = signQuizService.startPracticeSession(user.getId(),
                                savedSign.getSignCode());
                assertThat(practiceSession.questions())
                                .anySatisfy(this::assertReferencedSignNamesAreResolved);

                SignExamQuestionsDto examQuestions = signQuizService.getExamQuestions(savedSign.getSignCode(), 999);
                assertThat(examQuestions.questions()).singleElement()
                                .satisfies(this::assertReferencedSignNamesAreResolved);
        }

        private User createUser() {
                return createUser("sign-quiz-empty-user");
        }

        private User createUser(String prefix) {
                String suffix = UUID.randomUUID().toString().substring(0, 8);
                User user = new User();
                user.setUsername(prefix + "-" + suffix);
                user.setEmail(prefix + "-" + suffix + "@example.com");
                user.setFullName("Sign Quiz Empty User");
                user.setPasswordHash("dummy_hash");
                user.setRole(Role.USER);
                user.setIsActive(true);
                user.setIsLocked(false);
                return userRepository.saveAndFlush(user);
        }

        private void seedRandomPracticePool() {
                List<CanonicalSignCatalogService.CanonicalSignSeed> seeds = canonicalSignCatalogService
                                .getCanonicalSeeds();
                assertThat(seeds).hasSizeGreaterThanOrEqualTo(100);

                for (int i = 0; i < 100; i++) {
                        CanonicalSignCatalogService.CanonicalSignSeed seed = seeds.get(i);
                        RoadSign sign = roadSignRepository.findByNormalizedSignCode(seed.routeKey())
                                        .orElseGet(() -> createCanonicalSign(seed));

                        SignQuestion question = new SignQuestion();
                        question.setSign(sign);
                        question.setQuestionRef(seed.routeCode() + "_RANDOM_" + String.format("%03d", i + 1));
                        question.setQuestionType(SignQuestionType.WHAT_DOES_IT_MEAN);
                        question.setDifficulty(resolveDifficultyForIndex(i));
                        question.setIsCritical(false);
                        question.setShowSign(true);
                        question.setQuestionNl("Wat betekent dit bord? " + seed.routeCode());
                        question.setQuestionEn("What does this sign mean? " + seed.routeCode());
                        question.setQuestionFr("Que signifie ce panneau ? " + seed.routeCode());
                        question.setQuestionAr("ماذا تعني هذه الإشارة؟ " + seed.routeCode());
                        question.setExplanationNl("Uitleg " + seed.routeCode());
                        question.setExplanationEn("Explanation " + seed.routeCode());
                        question.setExplanationFr("Explication " + seed.routeCode());
                        question.setExplanationAr("شرح " + seed.routeCode());
                        question.setIsActive(true);
                        question.addChoice(createChoice("Juist", "Correct", "Correct", "صحيح", true, 1));
                        question.addChoice(createChoice("Fout", "Wrong", "Faux", "خطأ", false, 2));
                        question.addChoice(createChoice("Misschien", "Maybe", "Peut-être", "ربما", false, 3));
                        signQuestionRepository.saveAndFlush(question);
                }
        }

        private RoadSign createSignWithSingleQuestionExam() {
                RoadSign sign = new RoadSign();
                sign.setSignCode("A11EMPTYTEST");
                sign.setNormalizedSignCode("a11emptytest");
                sign.setCategory(SignCategory.DANGER);
                sign.setImagePath("/images/signs/danger_signs/A11 Uitweg op een kaai of een oever.png");
                sign.setSeriousViolation(false);
                sign.setNameNl("Testbord");
                sign.setNameEn("Test Sign");
                sign.setNameFr("Panneau test");
                sign.setNameAr("إشارة اختبار");
                sign.setDescriptionNl("Beschrijving");
                sign.setDescriptionEn("Description");
                sign.setDescriptionFr("Description");
                sign.setDescriptionAr("وصف");
                sign.setIsActive(true);

                SignQuestion question = new SignQuestion();
                question.setSign(sign);
                question.setQuestionRef("A11EMPTYTEST_Q01");
                question.setQuestionType(SignQuestionType.WHAT_DOES_IT_MEAN);
                question.setDifficulty(SignDifficulty.EASY);
                question.setIsCritical(false);
                question.setShowSign(true);
                question.setQuestionNl("Wat betekent dit bord?");
                question.setQuestionEn("What does this sign mean?");
                question.setQuestionFr("Que signifie ce panneau ?");
                question.setQuestionAr("ماذا تعني هذه الإشارة؟");
                question.setExplanationNl("Uitleg");
                question.setExplanationEn("Explanation");
                question.setExplanationFr("Explication");
                question.setExplanationAr("شرح");
                question.setIsActive(true);
                question.addChoice(createChoice("Juist", "Correct", "Correct", "صحيح", true, 1));
                question.addChoice(createChoice("Fout", "Wrong", "Faux", "خطأ", false, 2));
                question.addChoice(createChoice("Misschien", "Maybe", "Peut-etre", "ربما", false, 3));
                sign.getQuestions().add(question);

                SignExam exam = new SignExam();
                exam.setSign(sign);
                exam.setExamNumber(1);
                exam.setPassingScore(1);
                exam.setTotalQuestions(1);
                exam.setEasyCount(1);
                exam.setMediumCount(0);
                exam.setHardCount(0);
                exam.setIsActive(true);

                SignExamQuestion examQuestion = new SignExamQuestion();
                examQuestion.setQuestion(question);
                examQuestion.setQuestionOrder(1);
                exam.addExamQuestion(examQuestion);
                sign.getExams().add(exam);

                return roadSignRepository.saveAndFlush(sign);
        }

        private RoadSign createCanonicalSign(CanonicalSignCatalogService.CanonicalSignSeed seed) {
                RoadSign sign = new RoadSign();
                sign.setSignCode(seed.routeCode());
                sign.setNormalizedSignCode(seed.routeKey());
                sign.setCategory(seed.category());
                sign.setImagePath(seed.imagePath().startsWith("/") ? seed.imagePath().substring(1) : seed.imagePath());
                sign.setSeriousViolation(false);
                sign.setNameNl(seed.nameNl());
                sign.setNameEn(seed.nameEn());
                sign.setNameFr(seed.nameFr());
                sign.setNameAr(seed.nameAr());
                sign.setDescriptionNl(seed.descriptionNl());
                sign.setDescriptionEn(seed.descriptionEn());
                sign.setDescriptionFr(seed.descriptionFr());
                sign.setDescriptionAr(seed.descriptionAr());
                sign.setIsActive(true);
                return roadSignRepository.saveAndFlush(sign);
        }

        private RoadSign ensureCanonicalSignWithExam(CanonicalSignCatalogService.CanonicalSignSeed seed) {
                RoadSign sign = roadSignRepository.findByNormalizedSignCode(seed.routeKey())
                                .orElseGet(() -> createCanonicalSign(seed));

                signExamRepository.findBySignIdAndExamNumberAndIsActiveTrue(sign.getId(), 1)
                                .orElseGet(() -> {
                                        SignExam exam = new SignExam();
                                        exam.setSign(sign);
                                        exam.setExamNumber(1);
                                        exam.setPassingScore(6);
                                        exam.setTotalQuestions(8);
                                        exam.setEasyCount(3);
                                        exam.setMediumCount(3);
                                        exam.setHardCount(2);
                                        exam.setIsActive(true);
                                        return signExamRepository.saveAndFlush(exam);
                                });
                return sign;
        }

        private SignPracticeSession createPracticeSession(
                        User user,
                        RoadSign sign,
                        SignPracticeSession.SessionStatus status,
                        int totalQuestions,
                        int correctCount) {
                SignPracticeSession session = new SignPracticeSession();
                session.setUser(user);
                session.setSign(sign);
                session.setSignCode(sign.getSignCode());
                session.setStatus(status);
                session.setTotalQuestions(totalQuestions);
                session.setCorrectCount(correctCount);
                return session;
        }

        private SignExamResult createSignExamResult(
                        User user,
                        RoadSign sign,
                        double scorePct,
                        boolean passed) {
                SignExamResult result = new SignExamResult();
                result.setUserId(user.getId());
                result.setSignId(sign.getId());
                result.setSignCode(sign.getSignCode());
                result.setExamNumber(1);
                result.setTotalQuestions(8);
                result.setAnsweredCount(8);
                result.setCorrectCount((int) Math.round(scorePct * 8 / 100));
                result.setRequiredToPass(6);
                result.setScorePct(scorePct);
                result.setPassed(passed);
                result.setQuestionResultsJson("[]");
                return result;
        }

        private SignChoice createChoice(String textNl, String textEn, String textFr, String textAr, boolean isCorrect,
                        int displayOrder) {
                SignChoice choice = new SignChoice();
                choice.setTextNl(textNl);
                choice.setTextEn(textEn);
                choice.setTextFr(textFr);
                choice.setTextAr(textAr);
                choice.setIsCorrect(isCorrect);
                choice.setDisplayOrder(displayOrder);
                return choice;
        }

        private SignDifficulty resolveDifficultyForIndex(int index) {
                if (index < 40) {
                        return SignDifficulty.EASY;
                }
                if (index < 80) {
                        return SignDifficulty.MEDIUM;
                }
                return SignDifficulty.HARD;
        }

        private void assertDifficultyDistribution(List<SignQuizQuestionDto> questions) {
                Map<SignDifficulty, Long> counts = questions.stream()
                                .collect(Collectors.groupingBy(SignQuizQuestionDto::difficulty, Collectors.counting()));

                assertThat(counts.get(SignDifficulty.EASY)).isEqualTo(20);
                assertThat(counts.get(SignDifficulty.MEDIUM)).isEqualTo(20);
                assertThat(counts.get(SignDifficulty.HARD)).isEqualTo(10);
        }

        private List<Long> extractQuestionIds(List<SignQuizQuestionDto> questions) {
                return questions.stream().map(SignQuizQuestionDto::id).toList();
        }

        private void assertReferencedSignNamesAreResolved(SignQuizQuestionDto question) {
                String b11NameEn = roadSignRepository.findByNormalizedSignCode("b11")
                                .orElseThrow()
                                .getNameEn();
                String b17NameEn = roadSignRepository.findByNormalizedSignCode("b17")
                                .orElseThrow()
                                .getNameEn();

                assertThat(question.questionEn())
                                .contains(b11NameEn)
                                .contains(b17NameEn)
                                .doesNotContain("B11")
                                .doesNotContain("B17");
                assertThat(question.questionAr()).doesNotContain("B11").doesNotContain("B17");
                assertThat(question.questionNl()).doesNotContain("B11").doesNotContain("B17");
                assertThat(question.questionFr()).doesNotContain("B11").doesNotContain("B17");
                assertThat(question.choices()).allSatisfy(choice -> {
                        assertThat(choice.textEn()).doesNotContain("B11").doesNotContain("B17");
                        assertThat(choice.textAr()).doesNotContain("B11").doesNotContain("B17");
                        assertThat(choice.textNl()).doesNotContain("B11").doesNotContain("B17");
                        assertThat(choice.textFr()).doesNotContain("B11").doesNotContain("B17");
                });
        }

        private void ensureReferenceSignExists(
                        String signCode,
                        SignCategory category,
                        String nameEn,
                        String nameAr,
                        String nameFr,
                        String nameNl) {
                roadSignRepository.findByNormalizedSignCode(signCode.toLowerCase(Locale.ROOT))
                                .orElseGet(() -> {
                                        RoadSign sign = new RoadSign();
                                        sign.setSignCode(signCode);
                                        sign.setNormalizedSignCode(signCode.toLowerCase(Locale.ROOT));
                                        sign.setCategory(category);
                                        sign.setImagePath("/images/signs/test/" + signCode.toLowerCase(Locale.ROOT)
                                                        + ".png");
                                        sign.setSeriousViolation(false);
                                        sign.setNameEn(nameEn);
                                        sign.setNameAr(nameAr);
                                        sign.setNameFr(nameFr);
                                        sign.setNameNl(nameNl);
                                        sign.setDescriptionEn(nameEn);
                                        sign.setDescriptionAr(nameAr);
                                        sign.setDescriptionFr(nameFr);
                                        sign.setDescriptionNl(nameNl);
                                        sign.setIsActive(true);
                                        return roadSignRepository.saveAndFlush(sign);
                                });
        }

        private void assertRandomPracticeFailureNotification(Notification notification, Long sessionId) {
                assertThat(notification.getType()).isEqualTo(NotificationType.EXAM_FAILED);
                assertThat(notification.getMessageKey()).isEqualTo("notif.msg.sign_random_exam_failed");
                assertThat(notification.getLink())
                                .isEqualTo("/dashboard?section=exam-results&randomSignExamId=" + sessionId);
                assertThat(notification.getIsRead()).isFalse();
        }

        private void assertSavedAttempt(SignExamResult savedAttempt, Long userId, Long signId, String signCode) {
                assertThat(savedAttempt.getUserId()).isEqualTo(userId);
                assertThat(savedAttempt.getSignId()).isEqualTo(signId);
                assertThat(savedAttempt.getSignCode()).isEqualTo(signCode);
                assertThat(savedAttempt.getTotalQuestions()).isEqualTo(1);
                assertThat(savedAttempt.getAnsweredCount()).isZero();
                assertThat(savedAttempt.getCorrectCount()).isZero();
                assertThat(savedAttempt.getRequiredToPass()).isEqualTo(1);
                assertThat(savedAttempt.getScorePct()).isZero();
                assertThat(savedAttempt.getPassed()).isFalse();
        }
}
