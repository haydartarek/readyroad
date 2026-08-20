package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore;
import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore.CoverageRow;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.TheoryQuestionCoverageResponse;
import com.readyroad.readyroadbackend.dto.TheoryQuestionCoverageResponse.CategoryCoverage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TheoryQuestionCoverageService {

    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("ar", "nl", "en", "fr");

    private final UserRepository userRepository;
    private final TheoryQuestionCoverageStore coverageStore;

    public TheoryQuestionCoverageService(
            UserRepository userRepository,
            TheoryQuestionCoverageStore coverageStore) {
        this.userRepository = userRepository;
        this.coverageStore = coverageStore;
    }

    @Transactional(readOnly = true)
    public TheoryQuestionCoverageResponse getCoverage(long userId) {
        String languageCode = userRepository.findById(userId)
                .map(user -> supportedLanguage(user.getPreferredLanguage()))
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user does not exist"));
        List<CategoryCoverage> categories = coverageStore.findCoverage(userId, languageCode).stream()
                .map(TheoryQuestionCoverageService::toCategoryCoverage)
                .toList();

        long eligible = sum(categories, CategoryCoverage::getEligibleQuestions);
        long seen = sum(categories, CategoryCoverage::getUniqueQuestionsSeen);
        long presented = sum(categories, CategoryCoverage::getTimesPresented);
        long answered = sum(categories, CategoryCoverage::getTimesAnswered);
        long correct = sum(categories, CategoryCoverage::getTimesCorrect);
        long incorrect = sum(categories, CategoryCoverage::getTimesIncorrect);

        return TheoryQuestionCoverageResponse.builder()
                .languageCode(languageCode)
                .eligibleQuestions(eligible)
                .uniqueQuestionsSeen(seen)
                .unseenQuestions(Math.max(0, eligible - seen))
                .coveragePercentage(percentage(seen, eligible))
                .timesPresented(presented)
                .timesAnswered(answered)
                .timesCorrect(correct)
                .timesIncorrect(incorrect)
                .accuracyPercentage(percentage(correct, answered))
                .categories(categories)
                .build();
    }

    private static CategoryCoverage toCategoryCoverage(CoverageRow row) {
        long unseen = Math.max(0, row.eligibleQuestions() - row.uniqueQuestionsSeen());
        return CategoryCoverage.builder()
                .categoryId(row.categoryId())
                .categoryCode(row.categoryCode())
                .categoryName(row.categoryName())
                .eligibleQuestions(row.eligibleQuestions())
                .uniqueQuestionsSeen(row.uniqueQuestionsSeen())
                .unseenQuestions(unseen)
                .coveragePercentage(percentage(row.uniqueQuestionsSeen(), row.eligibleQuestions()))
                .timesPresented(row.timesPresented())
                .timesAnswered(row.timesAnswered())
                .timesCorrect(row.timesCorrect())
                .timesIncorrect(row.timesIncorrect())
                .accuracyPercentage(percentage(row.timesCorrect(), row.timesAnswered()))
                .build();
    }

    private static long sum(
            List<CategoryCoverage> categories,
            java.util.function.Function<CategoryCoverage, Long> extractor) {
        return categories.stream().map(extractor).mapToLong(value -> value == null ? 0 : value).sum();
    }

    private static BigDecimal percentage(long numerator, long denominator) {
        if (denominator <= 0) {
            return null;
        }
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    private static String supportedLanguage(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return SUPPORTED_LANGUAGES.contains(normalized) ? normalized : "en";
    }
}
