package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lessons")
public class Lesson extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleAr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleEn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleNl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleFr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contentAr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contentEn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contentNl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contentFr;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Integer estimatedMinutes = 5; // Estimated reading time

    @Column(nullable = false)
    private Boolean isActive = true;

    // Getters and Setters
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleNl() {
        return titleNl;
    }

    public void setTitleNl(String titleNl) {
        this.titleNl = titleNl;
    }

    public String getTitleFr() {
        return titleFr;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getContentAr() {
        return contentAr;
    }

    public void setContentAr(String contentAr) {
        this.contentAr = contentAr;
    }

    public String getContentEn() {
        return contentEn;
    }

    public void setContentEn(String contentEn) {
        this.contentEn = contentEn;
    }

    public String getContentNl() {
        return contentNl;
    }

    public void setContentNl(String contentNl) {
        this.contentNl = contentNl;
    }

    public String getContentFr() {
        return contentFr;
    }

    public void setContentFr(String contentFr) {
        this.contentFr = contentFr;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
