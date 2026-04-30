package com.readyroad.readyroadbackend.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.dto.QuizQuestionDTO;
import com.readyroad.readyroadbackend.service.RoadSignReferenceTextResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizQuestionMapperTrafficReferenceTest {

    @Mock
    private RoadSignRepository roadSignRepository;

    private QuizQuestionMapper quizQuestionMapper;

    @BeforeEach
    void setUp() {
        when(roadSignRepository.findAllByIsActiveTrue()).thenReturn(List.of(
                sign("C31a", "No left turn", "ممنوع الانعطاف إلى اليسار",
                        "Interdiction de tourner à gauche", "Verbod links afslaan"),
                sign("B11", "End of priority road", "نهاية طريق الأولوية",
                        "Fin de route prioritaire", "Einde van de voorrangsweg"),
                sign("B17", "Crossroads where priority from the right applies",
                        "تقاطع تسري فيه أولوية اليمين",
                        "Carrefour où la priorité de droite s'applique",
                        "Kruispunt waar voorrang van rechts geldt")));

        quizQuestionMapper = new QuizQuestionMapper(new RoadSignReferenceTextResolver(
                roadSignRepository,
                new ObjectMapper(),
                new DefaultResourceLoader()));
    }

    @Test
    void replacesRawSignCodeReferencesInTheoryQuestionAndOptions() {
        QuizQuestion question = new QuizQuestion();
        question.setQuestionAr("تقترب من تقاطع به علامة C31a لكن وجهتك على اليسار. ماذا تفعل؟");
        question.setQuestionEn("You approach sign C31a but your destination is on the left. What do you do?");
        question.setQuestionNl("U nadert bord C31a maar uw bestemming ligt links. Wat doet u?");
        question.setQuestionFr("Vous approchez du panneau C31a mais votre destination est à gauche. Que faites-vous ?");
        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.EASY);

        question.addOption(option(
                "U slaat linksaf omdat C31a alleen voor vrachtwagens geldt",
                "You turn left because C31a only applies to trucks",
                "Vous tournez à gauche car C31a ne s'applique qu'aux camions",
                "تنعطف يسارًا لأن C31a يخص الشاحنات فقط",
                false,
                1));
        question.addOption(option(
                "U volgt het verbod van bord C31a en slaat niet linksaf",
                "You obey sign C31a and do not turn left",
                "Vous respectez le panneau C31a et ne tournez pas à gauche",
                "تلتزم بالعلامة C31a ولا تنعطف يسارًا",
                true,
                2));
        question.addOption(option(
                "U wacht tot C31a niet meer zichtbaar is en slaat dan linksaf",
                "You wait until C31a is no longer visible and then turn left",
                "Vous attendez que C31a ne soit plus visible puis vous tournez à gauche",
                "تنتظر حتى تختفي C31a ثم تنعطف يسارًا",
                false,
                3));

        QuizQuestionDTO dto = quizQuestionMapper.toDTO(question);

        assertThat(dto.getQuestionEn()).contains("No left turn").doesNotContain("C31a");
        assertThat(dto.getQuestionAr()).contains("ممنوع الانعطاف إلى اليسار").doesNotContain("C31a");
        assertThat(dto.getQuestionNl()).contains("Verbod links afslaan").doesNotContain("C31a");
        assertThat(dto.getQuestionFr()).contains("Interdiction de tourner à gauche").doesNotContain("C31a");
        assertThat(dto.getOptions())
                .allSatisfy(option -> {
                    assertThat(option.getOptionTextEn()).doesNotContain("C31a");
                    assertThat(option.getOptionTextAr()).doesNotContain("C31a");
                    assertThat(option.getOptionTextNl()).doesNotContain("C31a");
                    assertThat(option.getOptionTextFr()).doesNotContain("C31a");
                });
    }

    @Test
    void replacesMultipleTrafficSignReferencesInTheoryQuestion() {
        QuizQuestion question = new QuizQuestion();
        question.setQuestionAr("بعد العلامة B11 تصل إلى تقاطع يحمل العلامة B17. هل تبقى لك أولوية اليمين؟");
        question.setQuestionEn(
                "After sign B11 you approach a junction with sign B17. Do you still have priority over traffic from the right?");
        question.setQuestionNl(
                "Na bord B11 nadert u een kruispunt met bord B17. Behoudt u daar nog voorrang op verkeer van rechts?");
        question.setQuestionFr(
                "Après le panneau B11 vous approchez d'un carrefour avec le panneau B17. Conservez-vous encore la priorité sur le trafic venant de droite ?");
        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);

        question.addOption(option("Ja", "Yes", "Oui", "نعم", false, 1));
        question.addOption(option("Nee", "No", "Non", "لا", true, 2));
        question.addOption(option("Alleen als er geen fietsers zijn", "Only if there are no cyclists",
                "Seulement s'il n'y a pas de cyclistes", "فقط إذا لم توجد دراجات", false, 3));

        QuizQuestionDTO dto = quizQuestionMapper.toDTO(question);

        assertThat(dto.getQuestionEn())
                .contains("End of priority road")
                .contains("Crossroads where priority from the right applies")
                .doesNotContain("B11")
                .doesNotContain("B17");
    }

    private static QuizAnswerOption option(
            String nl,
            String en,
            String fr,
            String ar,
            boolean isCorrect,
            int displayOrder) {
        QuizAnswerOption option = new QuizAnswerOption();
        option.setOptionTextNl(nl);
        option.setOptionTextEn(en);
        option.setOptionTextFr(fr);
        option.setOptionTextAr(ar);
        option.setIsCorrect(isCorrect);
        option.setDisplayOrder(displayOrder);
        return option;
    }

    private static RoadSign sign(String code, String en, String ar, String fr, String nl) {
        RoadSign sign = new RoadSign();
        sign.setSignCode(code);
        sign.setNameEn(en);
        sign.setNameAr(ar);
        sign.setNameFr(fr);
        sign.setNameNl(nl);
        sign.setIsActive(true);
        return sign;
    }
}
