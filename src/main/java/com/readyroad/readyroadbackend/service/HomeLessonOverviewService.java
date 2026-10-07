package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonCategory;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore;
import com.readyroad.readyroadbackend.dto.response.HomeLessonOverviewResponse;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HomeLessonOverviewService {

    private final LessonRepository lessonRepository;
    private final TheoryQuestionCoverageStore coverageStore;
    private final EntityManager entityManager;

    public HomeLessonOverviewService(
            LessonRepository lessonRepository,
            TheoryQuestionCoverageStore coverageStore,
            EntityManager entityManager) {
        this.lessonRepository = lessonRepository;
        this.coverageStore = coverageStore;
        this.entityManager = entityManager;
    }

    public List<HomeLessonOverviewResponse> getOverview() {
        return getOverview("en");
    }

    public List<HomeLessonOverviewResponse> getOverview(String languageCode) {
        List<Lesson> lessons =
                lessonRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc();

        if (lessons.isEmpty()) {
            return List.of();
        }

        List<Long> lessonIds = lessons.stream()
                .map(Lesson::getId)
                .toList();

        List<LessonCategory> categoryLinks = entityManager.createQuery(
                        """
                        SELECT lc
                        FROM LessonCategory lc
                        JOIN FETCH lc.lesson l
                        JOIN FETCH lc.category c
                        WHERE l.id IN :lessonIds
                          AND c.isActive = true
                        ORDER BY
                            l.id ASC,
                            lc.displayOrder ASC,
                            c.displayOrder ASC,
                            c.id ASC
                        """,
                        LessonCategory.class)
                .setParameter("lessonIds", lessonIds)
                .getResultList();

        Map<Long, List<LessonCategory>> categoriesByLesson =
                categoryLinks.stream()
                        .collect(Collectors.groupingBy(
                                link -> link.getLesson().getId()));

        Map<Long, Long> questionCountsByCategory =
                coverageStore.countEligibleQuestionsByCategory(languageCode);

        return lessons.stream()
                .map(lesson -> toResponse(
                        lesson,
                        categoriesByLesson.getOrDefault(
                                lesson.getId(),
                                List.of()),
                        questionCountsByCategory))
                .toList();
    }

    private HomeLessonOverviewResponse toResponse(
            Lesson lesson,
            List<LessonCategory> categoryLinks,
            Map<Long, Long> questionCountsByCategory) {

        List<HomeLessonOverviewResponse.CategorySummary> categories =
                categoryLinks.stream()
                        .map(link -> toCategorySummary(
                                link,
                                questionCountsByCategory))
                        .toList();

        return new HomeLessonOverviewResponse(
                lesson.getId(),
                lesson.getLessonCode(),
                lesson.getDisplayOrder(),
                lesson.getTitleNl(),
                lesson.getTitleEn(),
                lesson.getTitleFr(),
                lesson.getTitleAr(),
                categories);
    }

    private HomeLessonOverviewResponse.CategorySummary toCategorySummary(
            LessonCategory link,
            Map<Long, Long> questionCountsByCategory) {

        Category category = link.getCategory();

        long questionCount =
                questionCountsByCategory.getOrDefault(
                        category.getId(),
                        0L);

        return new HomeLessonOverviewResponse.CategorySummary(
                category.getCode(),
                category.getNameNl(),
                category.getNameEn(),
                category.getNameFr(),
                category.getNameAr(),
                questionCount,
                link.isPrimary(),
                link.getDisplayOrder());
    }
}
