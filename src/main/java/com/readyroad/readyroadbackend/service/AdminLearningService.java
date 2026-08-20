package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.TheoryQuestionCoverageResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.ActivityAvailability;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.CategoryPerformance;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.DifficultyPerformanceResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.ExamDetail;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.ExamSummary;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.PageResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.StudentSummary;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminLearningService {

    private final AdminLearningStore store;
    private final UserRepository userRepository;
    private final SignQuizService signQuizService;
    private final TheoryQuestionCoverageService coverageService;
    private final AdminTheoryExamHistoryService theoryExamHistoryService;

    @Transactional(readOnly = true)
    public PageResponse<StudentSummary> students(String query, int requestedPage, int requestedSize) {
        int page = safePage(requestedPage);
        int size = safeSize(requestedSize);
        long total = store.countStudents(query);
        List<StudentSummary> base = store.findStudents(query, size, page * size);
        Set<Long> ids = base.stream().map(StudentSummary::userId).collect(Collectors.toSet());
        Map<Long, List<Double>> scores = store.findRecentScores(ids);
        Map<Long, List<CategoryPerformance>> categories = store.findCategories(ids);
        List<StudentSummary> enriched = base.stream()
                .map(student -> enrich(student, scores.getOrDefault(student.userId(), List.of()),
                        categories.getOrDefault(student.userId(), List.of())))
                .toList();
        return page(enriched, total, page, size);
    }

    @Transactional(readOnly = true)
    public StudentSummary student(long userId) {
        requireStudent(userId);
        StudentSummary base = store.findStudent(userId);
        if (base == null) throw notFound();
        return enrich(base,
                store.findRecentScores(Set.of(userId)).getOrDefault(userId, List.of()),
                store.findCategories(Set.of(userId)).getOrDefault(userId, List.of()));
    }

    @Transactional(readOnly = true)
    public PageResponse<?> exams(Long userId, int requestedPage, int requestedSize) {
        if (userId != null) requireStudent(userId);
        int page = safePage(requestedPage);
        int size = safeSize(requestedSize);
        return page(store.findExams(userId, size, page * size), store.countExams(userId), page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<?> practices(long userId, int requestedPage, int requestedSize) {
        requireStudent(userId);
        int page = safePage(requestedPage);
        int size = safeSize(requestedSize);
        return page(store.findPractices(userId, size, page * size), store.countPractices(userId), page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<?> lessons(long userId, int requestedPage, int requestedSize) {
        requireStudent(userId);
        int page = safePage(requestedPage);
        int size = safeSize(requestedSize);
        return page(store.findLessons(userId, size, page * size), store.countLessons(userId), page, size);
    }

    @Transactional(readOnly = true)
    public List<CategoryPerformance> categories(long userId) {
        requireStudent(userId);
        return store.findCategories(Set.of(userId)).getOrDefault(userId, List.of());
    }

    @Transactional(readOnly = true)
    public TheoryQuestionCoverageResponse coverage(long userId) {
        requireStudent(userId);
        return coverageService.getCoverage(userId);
    }

    @Transactional(readOnly = true)
    public DifficultyPerformanceResponse difficulty(long userId) {
        requireStudent(userId);
        return store.findDifficultyPerformance(userId);
    }

    @Transactional(readOnly = true)
    public List<?> signs(long userId) {
        requireStudent(userId);
        return store.findSignPerformance(userId);
    }

    @Transactional(readOnly = true)
    public PageResponse<?> errorPatterns(long userId, int requestedPage, int requestedSize) {
        requireStudent(userId);
        int page = safePage(requestedPage);
        int size = safeSize(requestedSize);
        return page(store.findErrorPatterns(userId, size, page * size), store.countErrorPatterns(userId), page, size);
    }

    @Transactional(readOnly = true)
    public ActivityAvailability activityAvailability(long userId) {
        requireStudent(userId);
        return new ActivityAvailability(
                false,
                false,
                "Traffic-sign page views are not tracked by the current learning model.",
                "Video opens and watch progress are not tracked by the current learning model.");
    }

    @Transactional(readOnly = true)
    public ExamDetail examDetail(long userId, String examType, long examId) {
        requireStudent(userId);
        ExamSummary summary = store.findExam(userId, examType, examId);
        if (summary == null) throw notFound();

        String historicalContentStatus;
        Object detail;
        switch (examType) {
            case "THEORY_EXAM" -> {
                AdminTheoryExamHistoryService.HistoricalResult historical = theoryExamHistoryService.load(examId);
                historicalContentStatus = historical.status();
                detail = historical.result();
            }
            case "TRAFFIC_SIGN_EXAM" -> {
                historicalContentStatus = "STORED_RESULT";
                detail = signQuizService.getStoredSignExamResult(examId, userId);
            }
            case "RANDOM_EXAM" -> {
                historicalContentStatus = "STORED_RESULT";
                detail = signQuizService.getRandomSignPracticeResult(examId, userId);
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported exam type");
        }
        return new ExamDetail(userId, examType, examId, summary, historicalContentStatus, detail);
    }

    private User requireStudent(long userId) {
        User user = userRepository.findById(userId).orElseThrow(this::notFound);
        if (user.getRole() != Role.USER) throw notFound();
        return user;
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Student learning profile not found");
    }

    private static StudentSummary enrich(StudentSummary student, List<Double> recentScores,
            List<CategoryPerformance> categories) {
        List<CategoryPerformance> strongest = categories.stream()
                .sorted(Comparator.comparingDouble(CategoryPerformance::accuracy).reversed()
                        .thenComparing(Comparator.comparingInt(CategoryPerformance::questionsAttempted).reversed()))
                .limit(3).toList();
        List<CategoryPerformance> weakest = categories.stream()
                .sorted(Comparator.comparingDouble(CategoryPerformance::accuracy)
                        .thenComparing(Comparator.comparingInt(CategoryPerformance::questionsAttempted).reversed()))
                .limit(3).toList();
        return new StudentSummary(
                student.userId(), student.username(), student.displayName(), student.email(),
                student.preferredLanguage(), student.accountCreatedAt(), student.lastActiveAt(),
                student.totalCompletedExams(), student.totalCompletedPractices(), student.averageExamScore(),
                student.latestExamScore(), strongest, weakest, trend(recentScores), student.lastActivityType());
    }

    static String trend(List<Double> newestFirstScores) {
        if (newestFirstScores.size() < 2) return "INSUFFICIENT_DATA";
        int recentSize = newestFirstScores.size() / 2;
        double recent = newestFirstScores.subList(0, recentSize).stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double previous = newestFirstScores.subList(recentSize, newestFirstScores.size()).stream()
                .mapToDouble(Double::doubleValue).average().orElse(0);
        int comparison = Double.compare(recent, previous);
        return comparison > 0 ? "IMPROVING" : comparison < 0 ? "DECLINING" : "STABLE";
    }

    private static int safePage(int page) {
        return Math.max(0, page);
    }

    private static int safeSize(int size) {
        return Math.max(1, Math.min(size, 100));
    }

    private static <T> PageResponse<T> page(List<T> items, long total, int page, int size) {
        int totalPages = total == 0 ? 0 : (int) Math.ceil(total / (double) size);
        return new PageResponse<>(items, total, page, size, totalPages);
    }
}
