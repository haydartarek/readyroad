package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.entity.SignChoice;
import com.readyroad.readyroadbackend.domain.entity.SignExam;
import com.readyroad.readyroadbackend.domain.entity.SignExamQuestion;
import com.readyroad.readyroadbackend.domain.entity.SignExamResult;
import com.readyroad.readyroadbackend.domain.entity.SignQuestion;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamResultRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.sign.SignExamResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SignQuizServiceIntegrationTest {

    @Autowired
    private SignQuizService signQuizService;

    @Autowired
    private RoadSignRepository roadSignRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SignExamResultRepository signExamResultRepository;

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
                .satisfies(savedAttempt -> assertSavedAttempt(savedAttempt, user.getId(), sign.getId(), sign.getSignCode()));
    }

    private User createUser() {
        User user = new User();
        user.setUsername("sign-quiz-empty-user");
        user.setEmail("sign-quiz-empty-user@example.com");
        user.setFullName("Sign Quiz Empty User");
        user.setPasswordHash("dummy_hash");
        user.setRole(Role.USER);
        user.setIsActive(true);
        user.setIsLocked(false);
        return userRepository.saveAndFlush(user);
    }

    private RoadSign createSignWithSingleQuestionExam() {
        RoadSign sign = new RoadSign();
        sign.setSignCode("A11EMPTYTEST");
        sign.setNormalizedSignCode("a11emptytest");
        sign.setCategory(SignCategory.DANGER);
        sign.setImagePath("/images/signs/test/a11-empty.png");
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

    private SignChoice createChoice(String textNl, String textEn, String textFr, String textAr, boolean isCorrect, int displayOrder) {
        SignChoice choice = new SignChoice();
        choice.setTextNl(textNl);
        choice.setTextEn(textEn);
        choice.setTextFr(textFr);
        choice.setTextAr(textAr);
        choice.setIsCorrect(isCorrect);
        choice.setDisplayOrder(displayOrder);
        return choice;
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
