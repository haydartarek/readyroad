package com.readyroad.readyroadbackend.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
import com.readyroad.readyroadbackend.dto.response.LessonDetailResponse;
import com.readyroad.readyroadbackend.dto.response.LessonPageResponse;
import com.readyroad.readyroadbackend.dto.response.LessonResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class LessonMapper {

    private final ObjectMapper objectMapper;

    public LessonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** Map entity → list/summary response (no pages). */
    public LessonResponse toResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getLessonCode(),
                lesson.getIcon(),
                lesson.getTitleNl(),
                lesson.getTitleEn(),
                lesson.getTitleFr(),
                lesson.getTitleAr(),
                lesson.getDescriptionNl(),
                lesson.getDescriptionEn(),
                lesson.getDescriptionFr(),
                lesson.getDescriptionAr(),
                lesson.getDisplayOrder(),
                lesson.getEstimatedMinutes(),
                lesson.getPages().size());
    }

    /** Map entity → full detail response (with pages). */
    public LessonDetailResponse toDetailResponse(Lesson lesson) {
        List<LessonPageResponse> pages = lesson.getPages().stream()
                .map(this::toPageResponse)
                .toList();

        return new LessonDetailResponse(
                lesson.getId(),
                lesson.getLessonCode(),
                lesson.getIcon(),
                lesson.getTitleNl(),
                lesson.getTitleEn(),
                lesson.getTitleFr(),
                lesson.getTitleAr(),
                lesson.getDescriptionNl(),
                lesson.getDescriptionEn(),
                lesson.getDescriptionFr(),
                lesson.getDescriptionAr(),
                lesson.getDisplayOrder(),
                lesson.getEstimatedMinutes(),
                pages);
    }

    /** Map page entity → page response. */
    public LessonPageResponse toPageResponse(LessonPage page) {
        return new LessonPageResponse(
                page.getId(),
                page.getPageNumber(),
                page.getTitleNl(),
                page.getTitleEn(),
                page.getTitleFr(),
                page.getTitleAr(),
                page.getContentNl(),
                page.getContentEn(),
                page.getContentFr(),
                page.getContentAr(),
                parseJsonArray(page.getBulletPointsNl()),
                parseJsonArray(page.getBulletPointsEn()),
                parseJsonArray(page.getBulletPointsFr()),
                parseJsonArray(page.getBulletPointsAr()));
    }

    /**
     * Parse a JSON array string into a List&lt;String&gt;.
     * Returns empty list on null or parse failure.
     */
    private List<String> parseJsonArray(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
