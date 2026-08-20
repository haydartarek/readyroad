package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore;
import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore.CoverageRow;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.TheoryQuestionCoverageResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TheoryQuestionCoverageServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TheoryQuestionCoverageStore coverageStore;

    private TheoryQuestionCoverageService service;

    @BeforeEach
    void setUp() {
        service = new TheoryQuestionCoverageService(userRepository, coverageStore);
    }

    @Test
    void keepsCoverageAndAccuracyIndependentAndAggregatesHistoryCounters() {
        long userId = 42L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user("ar")));
        when(coverageStore.findCoverage(userId, "ar")).thenReturn(List.of(
                row(101L, "TH01", "الأولوية", 6, 3, 5, 3, 2, 1),
                row(102L, "TH02", "السرعة", 4, 1, 2, 0, 0, 0)));

        TheoryQuestionCoverageResponse result = service.getCoverage(userId);

        assertThat(result.getLanguageCode()).isEqualTo("ar");
        assertThat(result.getEligibleQuestions()).isEqualTo(10L);
        assertThat(result.getUniqueQuestionsSeen()).isEqualTo(4L);
        assertThat(result.getUnseenQuestions()).isEqualTo(6L);
        assertThat(result.getCoveragePercentage()).isEqualByComparingTo("40.00");
        assertThat(result.getTimesPresented()).isEqualTo(7L);
        assertThat(result.getTimesAnswered()).isEqualTo(3L);
        assertThat(result.getTimesCorrect()).isEqualTo(2L);
        assertThat(result.getTimesIncorrect()).isEqualTo(1L);
        assertThat(result.getAccuracyPercentage()).isEqualByComparingTo("66.67");

        TheoryQuestionCoverageResponse.CategoryCoverage first = result.getCategories().getFirst();
        assertThat(first.getCoveragePercentage()).isEqualByComparingTo("50.00");
        assertThat(first.getAccuracyPercentage()).isEqualByComparingTo("66.67");
    }

    @Test
    void reportsRawSmallSampleAccuracyWithoutInventingMastery() {
        long userId = 9L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user("en")));
        when(coverageStore.findCoverage(userId, "en")).thenReturn(List.of(
                row(101L, "TH01", "Priority", 12, 1, 1, 1, 1, 0)));

        TheoryQuestionCoverageResponse.CategoryCoverage category =
                service.getCoverage(userId).getCategories().getFirst();

        assertThat(category.getAccuracyPercentage()).isEqualByComparingTo("100.00");
        assertThat(category.getTimesAnswered()).isEqualTo(1L);
        assertThat(Arrays.stream(category.getClass().getDeclaredFields())
                .map(java.lang.reflect.Field::getName))
                .doesNotContain("mastery", "mastered", "masteryLevel");
    }

    @ParameterizedTest
    @MethodSource("supportedLanguages")
    void usesTheUsersSupportedLanguageForTheEligibleDenominator(String stored, String expected) {
        long userId = 17L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user(stored)));
        when(coverageStore.findCoverage(userId, expected)).thenReturn(List.of());

        TheoryQuestionCoverageResponse result = service.getCoverage(userId);

        assertThat(result.getLanguageCode()).isEqualTo(expected);
        verify(coverageStore).findCoverage(userId, expected);
    }

    private static Stream<Arguments> supportedLanguages() {
        return Stream.of(
                Arguments.of("ar", "ar"),
                Arguments.of("NL", "nl"),
                Arguments.of("fr", "fr"),
                Arguments.of("en", "en"),
                Arguments.of(null, "en"),
                Arguments.of("unsupported", "en"));
    }

    private static User user(String language) {
        User user = new User();
        user.setPreferredLanguage(language);
        return user;
    }

    private static CoverageRow row(
            long categoryId,
            String code,
            String name,
            long eligible,
            long seen,
            long presented,
            long answered,
            long correct,
            long incorrect) {
        return new CoverageRow(
                categoryId,
                code,
                name,
                eligible,
                seen,
                presented,
                answered,
                correct,
                incorrect);
    }
}
