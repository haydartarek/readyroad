package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.dto.response.LessonResponse;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public LessonResponse toResponse(Lesson lesson, Integer questionsCount) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getCategory().getId(),
                lesson.getCategory().getCode(),
                lesson.getTitleAr(),
                lesson.getTitleEn(),
                lesson.getTitleNl(),
                lesson.getTitleFr(),
                lesson.getContentAr(),
                lesson.getContentEn(),
                lesson.getContentNl(),
                lesson.getContentFr(),
                lesson.getDisplayOrder(),
                lesson.getEstimatedMinutes(),
                questionsCount
        );
    }
}
