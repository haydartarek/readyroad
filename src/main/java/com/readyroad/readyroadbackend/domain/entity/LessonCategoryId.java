package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LessonCategoryId implements Serializable {

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "category_id")
    private Long categoryId;

    public LessonCategoryId() {
    }

    public LessonCategoryId(Long lessonId, Long categoryId) {
        this.lessonId = lessonId;
        this.categoryId = categoryId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LessonCategoryId that)) {
            return false;
        }

        return Objects.equals(lessonId, that.lessonId)
                && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonId, categoryId);
    }
}