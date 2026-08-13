package com.readyroad.readyroadbackend.marketing.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class ContentSourceServiceTest {

    private RoadSignRepository signs;
    private LessonRepository lessons;
    private QuizQuestionRepository questions;
    private ContentSourceService service;

    @BeforeEach
    void setUp() {
        signs = mock(RoadSignRepository.class);
        lessons = mock(LessonRepository.class);
        questions = mock(QuizQuestionRepository.class);
        service = new ContentSourceService(
                signs, lessons, questions, mock(JdbcTemplate.class), new MarketingProperties());
    }

    @Test
    void readsOnlyVerifiedFourLanguageRoadSignFields() {
        RoadSign sign = new RoadSign();
        sign.setNameAr("علامة عربية");
        sign.setDescriptionAr("وصف عربي");
        sign.setNameNl("Nederlands bord");
        sign.setDescriptionNl("Nederlandse uitleg");
        sign.setNameEn("English sign");
        sign.setDescriptionEn("English explanation");
        sign.setNameFr("Panneau français");
        sign.setDescriptionFr("Explication française");
        when(signs.findFirstActiveBySignCodeCaseSensitive("A1")).thenReturn(Optional.of(sign));

        VerifiedContentSource source = service.load(ContentSourceType.ROAD_SIGN, "A1");

        assertThat(source.sourceReference()).isEqualTo("ROAD_SIGN:A1");
        assertThat(source.facts()).hasSize(4);
        assertThat(source.factsFor(ContentLocale.FR).facts()).contains("Explication française");
    }

    @Test
    void readsLessonPagesInEveryLanguageWithoutChangingCoreData() {
        Lesson lesson = new Lesson();
        lesson.setIsActive(true);
        lesson.setTitleAr("درس عربي");
        lesson.setDescriptionAr("مقدمة عربية");
        lesson.setTitleNl("Nederlandse les");
        lesson.setDescriptionNl("Nederlandse intro");
        lesson.setTitleEn("English lesson");
        lesson.setDescriptionEn("English intro");
        lesson.setTitleFr("Leçon française");
        lesson.setDescriptionFr("Introduction française");
        LessonPage page = new LessonPage();
        page.setPageNumber(1);
        page.setTitleAr("صفحة");
        page.setContentAr("معلومة عربية موثقة");
        page.setTitleNl("Pagina");
        page.setContentNl("Nederlandse geverifieerde informatie");
        page.setTitleEn("Page");
        page.setContentEn("Verified English information");
        page.setTitleFr("Page");
        page.setContentFr("Information française vérifiée");
        lesson.setPages(java.util.List.of(page));
        when(lessons.findByLessonCode("les-1")).thenReturn(Optional.of(lesson));

        VerifiedContentSource source = service.load(ContentSourceType.LESSON, "les-1");

        assertThat(source.factsFor(ContentLocale.AR).facts()).contains("معلومة عربية موثقة");
        assertThat(source.factsFor(ContentLocale.EN).facts()).contains("Verified English information");
    }

    @Test
    void readsPublishedQuestionAndItsSingleVerifiedCorrectAnswer() {
        QuizQuestion question = new QuizQuestion();
        question.setIsActive(true);
        question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.EASY);
        question.setQuestionAr("سؤال عربي؟");
        question.setQuestionNl("Nederlandse vraag?");
        question.setQuestionEn("English question?");
        question.setQuestionFr("Question française ?");
        question.setExplanationAr("شرح عربي");
        question.setExplanationNl("Nederlandse uitleg");
        question.setExplanationEn("English explanation");
        question.setExplanationFr("Explication française");
        QuizAnswerOption answer = new QuizAnswerOption();
        answer.setIsActive(true);
        answer.setIsCorrect(true);
        answer.setDisplayOrder(1);
        answer.setOptionTextAr("جواب");
        answer.setOptionTextNl("Antwoord");
        answer.setOptionTextEn("Answer");
        answer.setOptionTextFr("Réponse");
        question.setOptions(java.util.List.of(answer));
        when(questions.findByIdWithOptions(42L)).thenReturn(Optional.of(question));

        VerifiedContentSource source = service.load(ContentSourceType.QUESTION, "42");

        assertThat(source.factsFor(ContentLocale.NL).facts()).contains("Antwoord", "Nederlandse uitleg");
        assertThat(source.factsFor(ContentLocale.FR).facts()).contains("Réponse", "Explication française");
    }
}
