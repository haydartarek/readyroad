package com.readyroad.readyroadbackend.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrivingTextSanitizerTest {

    private static final String LEGACY_SERIES_WORD = "الس" + "لسلة";
    private static final String LATIN_A = "A";
    private static final String LATIN_B = "B";
    private static final String LATIN_D = "D";

    @Test
    void keepsAlreadyNormalizedArabicTrafficSignPhraseIntact() {
        String value = "العلامة المرورية";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized).isEqualTo("العلامة المرورية");
    }

    @Test
    void normalizesLegacyArabicTrafficSignPromptWithoutDoubleMutation() {
        String value = "ما معنى علامة المرور هذه؟";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized).isEqualTo("ما معنى هذه العلامة المرورية؟");
    }

    @Test
    void repairsPreviouslyCorruptedArabicTrafficSignToken() {
        String value = "هذه الالعلامة المروريةية مهمة";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized).isEqualTo("هذه العلامة المرورية مهمة");
    }

    @Test
    void removesForbiddenSeriesWordFromArabicCurveContent() {
        String value = "تحذر هذه العلامة المرورية من " + LEGACY_SERIES_WORD
                + " منعطفات خطيرة متتالية يكون أولها إلى اليسار. اضبط سرعتك لكامل "
                + LEGACY_SERIES_WORD + ".";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized)
                .isEqualTo(
                        "تحذر هذه العلامة المرورية من منعطفات خطيرة متتالية يكون أولها إلى اليسار. اضبط سرعتك على امتداد جميع المنعطفات.");
    }

    @Test
    void removesLatinCategoryLettersFromArabicCategoryExplanation() {
        String value = "علامات الخطر (" + LATIN_A + ") مثلثة؛ علامات الإلزام (" + LATIN_D + ") زرقاء.";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized).isEqualTo("علامات الخطر مثلثة؛ وعلامات الإلزام زرقاء.");
    }

    @Test
    void rewritesArabicMopedClassLettersWithoutLatinCharacters() {
        String value = "تحظر C9 الدراجات البخارية الصغيرة فقط (فئة " + LATIN_A
                + ": أقصى 45 كم/ساعة، وفئة " + LATIN_B + ": الدراجات الكهربائية السريعة).";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized)
                .isEqualTo(
                        "تحظر C9 الدراجات البخارية الصغيرة فقط (الفئة أ: أقصى 45 كم/ساعة، والفئة ب: الدراجات الكهربائية السريعة).");
    }

    @Test
    void removesDuplicatedArabicTrafficSignPrefix() {
        String value = "ترى اللافتة العلامة المرورية: وجود إشارة ضوئية. كيف تعدّل سلوكك القيادي؟";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized)
                .isEqualTo("ترى العلامة المرورية التي تشير إلى وجود إشارة ضوئية. كيف تعدّل سلوكك أثناء القيادة؟");
    }

    @Test
    void normalizesArabicDangerSignLabels() {
        String value = "تنتمي هذه العلامة المرورية إلى لافتات الخطر. جميع لافتات الخطر مثلثة.";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized).isEqualTo("تنتمي هذه العلامة المرورية إلى علامات الخطر. جميع علامات الخطر مثلثة.");
    }

    @Test
    void rewritesArabicLegacyLafitaQuestionIntoTrafficSignQuestion() {
        String value = "أي لافتة تُشير إلى انتهاء طريق الأولوية؟";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized).isEqualTo("أي علامة مرورية تُشير إلى انتهاء طريق الأولوية؟");
    }

    @Test
    void removesArabicSeriesWordFromRoadSignQuestion() {
        String value = "هل أنت ملزم قانونياً بتقليل سرعتك قبل دخول المنعطف الأول من السلسلة؟";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized)
                .isEqualTo("هل أنت ملزم قانونياً بتقليل سرعتك قبل دخول المنعطف الأول من المنعطفات المتتالية؟");
    }

    @Test
    void rewritesArabicSequenceWordInBendScenario() {
        String value = "أنت تسير بسرعة 90 كم/ساعة ليلاً وترى هذه العلامة المرورية التي تعلن عن سلسلة منعطفات، الأول إلى اليمين. ماذا تفعل أولاً؟";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized)
                .isEqualTo(
                        "أنت تقود ليلًا بسرعة 90 كم/ساعة، وترى هذه العلامة المرورية التي تشير إلى تعاقب منعطفات أولها إلى اليمين. ماذا تفعل أولاً؟");
    }

    @Test
    void rewritesLegacyArabicChildrenWarningScenario() {
        String value = "تقترب من العلامة المرورية: تحذير: أطفال. الساعة 15:30 يوم عمل وانتهى الدوام المدرسي للتو. كيف تقود؟";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized)
                .isEqualTo("أمامك علامة تحذير: أطفال. الساعة 15:30 بعد انتهاء المدرسة. كيف تتصرف أثناء القيادة؟");
    }

    @Test
    void normalizesArabicB15PriorityExplanation() {
        String value = "تستخدم هذه الفئة15 رموزاً لإظهار تكوين الطريق والإشارة إلى حق أولويتك على طرق جانبية محددة.";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized)
                .isEqualTo("تستخدم هذه العلامة رمزًا يوضح شكل الطريق لتبيّن حق الأولوية على الطريق الجانبي المحدد.");
    }

    @Test
    void replacesMalformedArabicB15NameWithGenericFallback() {
        String value = "الأولوية على الطريق الجانبي اليسار";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized).isEqualTo("أولوية على طريق جانبي متقاطع");
    }

    @Test
    void convertsEnglishSeriesQuestionIntoCategoryQuestion() {
        String value = "To which series does the traffic sign \"No parking\" belong?";

        String sanitized = DrivingTextSanitizer.sanitize("EN", value);

        assertThat(sanitized).isEqualTo("To which category does the traffic sign \"No parking\" belong?");
    }

    @Test
    void removesTrailingEnglishCodeAfterResolvedSignName() {
        String value = "A sign from the the traffic sign \"Expressway\"9/F101 series that opened the reserved road";

        String sanitized = DrivingTextSanitizer.sanitize("EN", value);

        assertThat(sanitized)
                .isEqualTo("A sign from the traffic sign \"Expressway\" that opened the reserved road");
    }

    @Test
    void removesEnglishSeriesLabelFromSignTypeExplanation() {
        String value = "Yes: stopping is never regulated by E-series signs";

        String sanitized = DrivingTextSanitizer.sanitize("EN", value);

        assertThat(sanitized).isEqualTo("Yes: stopping is never regulated by parking and stopping signs");
    }

    @Test
    void repairsEnglishBendChoiceGrammar() {
        String value = "A consecutive dangerous bends where the first is to the left";

        String sanitized = DrivingTextSanitizer.sanitize("EN", value);

        assertThat(sanitized).isEqualTo("A succession of dangerous bends where the first is to the left");
    }

    @Test
    void rewritesLegacyEnglishChildrenWarningScenario() {
        String value = "You approach the traffic sign \"Warning: children\". It is 15:30 on a weekday and school has just ended. How do you drive?";

        String sanitized = DrivingTextSanitizer.sanitize("EN", value);

        assertThat(sanitized)
                .isEqualTo(
                        "You are approaching a children warning sign. It is 15:30, just after school has ended. How should you drive?");
    }

    @Test
    void rewritesLegacyArabicCyclistCrossingComparisonPrompt() {
        String value = "ما الفرق بين العلامة المرورية: منطقة عبور المشاة (ممر عبور المشاة) و العلامة المرورية: منطقة عبور الدراجات والدراجات البخارية (ممر عبور الدراجات)؟";

        String sanitized = DrivingTextSanitizer.sanitize("AR", value);

        assertThat(sanitized).isEqualTo("ما الفرق بين ممر عبور المشاة وممر عبور الدراجات؟");
    }

    @Test
    void rewritesLegacyEnglishCyclistCrossingComparisonPrompt() {
        String value = "What is the difference between the traffic sign \"Pedestrian crossing\" (pedestrian crossing) and the traffic sign \"Bicycle and moped crossing\" (cyclist crossing)?";

        String sanitized = DrivingTextSanitizer.sanitize("EN", value);

        assertThat(sanitized).isEqualTo("What is the difference between a pedestrian crossing and a cyclist crossing?");
    }

    @Test
    void passesThrough_CanonicalEnglishB15ehoiceLabel() {
        String value = "Priority over intersecting side road";

        String sanitized = DrivingTextSanitizer.sanitize("EN", value);

        assertThat(sanitized).isEqualTo("Priority over intersecting side road");
    }

    @Test
    void rewritesLegacyEnglishCyclistCrossingActionPrompt() {
        String value = "A cyclist is approaching the crossing from the right, indicated by the traffic sign \"Bicycle and moped crossing\". What do you do?";

        String sanitized = DrivingTextSanitizer.sanitize("EN", value);

        assertThat(sanitized)
                .isEqualTo("A cyclist is approaching the cyclist crossing from the right. What do you do?");
    }

    @Test
    void convertsDutchSeriesQuestionIntoCategoryQuestion() {
        String value = "Tot welke reeks behoort het verkeersbord \"Parkeerverbod\"?";

        String sanitized = DrivingTextSanitizer.sanitize("NL", value);

        assertThat(sanitized).isEqualTo("Tot welke categorie behoort het verkeersbord \"Parkeerverbod\"?");
    }

    @Test
    void repairsDutchBendChoiceGrammar() {
        String value = "Een opeenvolgende gevaarlijke bochten waarbij de eerste naar rechts gaat";

        String sanitized = DrivingTextSanitizer.sanitize("NL", value);

        assertThat(sanitized).isEqualTo("Opeenvolgende gevaarlijke bochten waarvan de eerste naar rechts gaat");
    }

    @Test
    void convertsFrenchSeriesQuestionIntoCategoryQuestion() {
        String value = "A quelle serie appartient le panneau \"Interdiction de stationnement\" ?";

        String sanitized = DrivingTextSanitizer.sanitize("FR", value);

        assertThat(sanitized).isEqualTo("A quelle catégorie appartient le panneau \"Interdiction de stationnement\"?");
    }

    @Test
    void repairsFrenchBendChoiceGrammar() {
        String value = "Une virages dangereux consécutifs dont le premier est à droite";

        String sanitized = DrivingTextSanitizer.sanitize("FR", value);

        assertThat(sanitized).isEqualTo("Une succession de virages dangereux dont le premier est à droite");
    }
}
