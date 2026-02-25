package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lesson_pages", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "lesson_id", "page_number" })
})
public class LessonPage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "page_number", nullable = false)
    private Integer pageNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleNl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleEn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleFr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleAr;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String contentNl;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String contentEn;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String contentFr;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String contentAr;

    /** JSON array of bullet points, stored as TEXT */
    @Column(columnDefinition = "TEXT")
    private String bulletPointsNl;

    @Column(columnDefinition = "TEXT")
    private String bulletPointsEn;

    @Column(columnDefinition = "TEXT")
    private String bulletPointsFr;

    @Column(columnDefinition = "TEXT")
    private String bulletPointsAr;

    // ─── Getters and Setters ─────────────────────────────

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getTitleNl() {
        return titleNl;
    }

    public void setTitleNl(String titleNl) {
        this.titleNl = titleNl;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleFr() {
        return titleFr;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getContentNl() {
        return contentNl;
    }

    public void setContentNl(String contentNl) {
        this.contentNl = contentNl;
    }

    public String getContentEn() {
        return contentEn;
    }

    public void setContentEn(String contentEn) {
        this.contentEn = contentEn;
    }

    public String getContentFr() {
        return contentFr;
    }

    public void setContentFr(String contentFr) {
        this.contentFr = contentFr;
    }

    public String getContentAr() {
        return contentAr;
    }

    public void setContentAr(String contentAr) {
        this.contentAr = contentAr;
    }

    public String getBulletPointsNl() {
        return bulletPointsNl;
    }

    public void setBulletPointsNl(String bulletPointsNl) {
        this.bulletPointsNl = bulletPointsNl;
    }

    public String getBulletPointsEn() {
        return bulletPointsEn;
    }

    public void setBulletPointsEn(String bulletPointsEn) {
        this.bulletPointsEn = bulletPointsEn;
    }

    public String getBulletPointsFr() {
        return bulletPointsFr;
    }

    public void setBulletPointsFr(String bulletPointsFr) {
        this.bulletPointsFr = bulletPointsFr;
    }

    public String getBulletPointsAr() {
        return bulletPointsAr;
    }

    public void setBulletPointsAr(String bulletPointsAr) {
        this.bulletPointsAr = bulletPointsAr;
    }
}
