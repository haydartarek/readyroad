package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
import com.readyroad.readyroadbackend.dto.response.LessonDetailResponse;
import com.readyroad.readyroadbackend.dto.response.LessonPageResponse;
import com.readyroad.readyroadbackend.dto.response.LessonResponse;
import com.readyroad.readyroadbackend.storage.MediaUrlResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LessonMapper {

    private final MediaUrlResolver mediaUrlResolver;

    @Autowired
    public LessonMapper(MediaUrlResolver mediaUrlResolver) {
        this.mediaUrlResolver = mediaUrlResolver;
    }

    /** Compatibility constructor for small unit tests that only map non-media fields. */
    public LessonMapper() {
        this.mediaUrlResolver = null;
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
                toLessonImageUrl(page));
    }

    private String toLessonImageUrl(
            LessonPage page) {

        if (page.getImageAsset() == null) {
            return null;
        }

        String storageKey =
                page.getImageAsset()
                        .getStorageKey();

        if (storageKey == null
                || storageKey.isBlank()) {
            return null;
        }

        return mediaUrlResolver == null
                ? (storageKey.startsWith("/images/") ? storageKey : "/images/" + storageKey)
                : mediaUrlResolver.resolvePublicUrl(storageKey);
    }
}
