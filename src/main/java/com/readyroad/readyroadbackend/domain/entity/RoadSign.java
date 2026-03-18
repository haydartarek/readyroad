package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.util.TextNormalizer;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A Belgian road sign in the Sign Quiz System.
 * Independent of the legacy {@code traffic_signs} table.
 */
@Entity
@Table(name = "road_signs")
public class RoadSign extends BaseEntity {

    @Column(name = "sign_code", nullable = false, length = 50)
    private String signCode;

    @Column(name = "normalized_sign_code", nullable = false, length = 100)
    private String normalizedSignCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SignCategory category;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    @Column(name = "serious_violation", nullable = false)
    private Boolean seriousViolation = false;

    // ── Multilingual name ────────────────────────────────────────────────────
    @Column(name = "name_nl", columnDefinition = "TEXT")
    private String nameNl;

    @Column(name = "name_en", columnDefinition = "TEXT")
    private String nameEn;

    @Column(name = "name_fr", columnDefinition = "TEXT")
    private String nameFr;

    @Column(name = "name_ar", columnDefinition = "TEXT")
    private String nameAr;

    // ── Multilingual description ─────────────────────────────────────────────
    @Column(name = "description_nl", columnDefinition = "TEXT")
    private String descriptionNl;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_fr", columnDefinition = "TEXT")
    private String descriptionFr;

    @Column(name = "description_ar", columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // ── Relationships ────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "sign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SignQuestion> questions = new ArrayList<>();

    @OneToMany(mappedBy = "sign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SignExam> exams = new ArrayList<>();

    // ── Text normalisation ───────────────────────────────────────────────────
    @Override
    protected void normalizeTextFields() {
        nameNl = TextNormalizer.normalize(nameNl);
        nameEn = TextNormalizer.normalize(nameEn);
        nameFr = TextNormalizer.normalize(nameFr);
        nameAr = TextNormalizer.normalize(nameAr);
        descriptionNl = TextNormalizer.normalize(descriptionNl);
        descriptionEn = TextNormalizer.normalize(descriptionEn);
        descriptionFr = TextNormalizer.normalize(descriptionFr);
        descriptionAr = TextNormalizer.normalize(descriptionAr);
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String getSignCode() {
        return signCode;
    }

    public void setSignCode(String v) {
        this.signCode = v;
    }

    public String getNormalizedSignCode() {
        return normalizedSignCode;
    }

    public void setNormalizedSignCode(String v) {
        this.normalizedSignCode = v;
    }

    public SignCategory getCategory() {
        return category;
    }

    public void setCategory(SignCategory v) {
        this.category = v;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String v) {
        this.imagePath = v;
    }

    public Boolean getSeriousViolation() {
        return seriousViolation;
    }

    public void setSeriousViolation(Boolean v) {
        this.seriousViolation = v;
    }

    public String getNameNl() {
        return nameNl;
    }

    public void setNameNl(String v) {
        this.nameNl = v;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String v) {
        this.nameEn = v;
    }

    public String getNameFr() {
        return nameFr;
    }

    public void setNameFr(String v) {
        this.nameFr = v;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String v) {
        this.nameAr = v;
    }

    public String getDescriptionNl() {
        return descriptionNl;
    }

    public void setDescriptionNl(String v) {
        this.descriptionNl = v;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String v) {
        this.descriptionEn = v;
    }

    public String getDescriptionFr() {
        return descriptionFr;
    }

    public void setDescriptionFr(String v) {
        this.descriptionFr = v;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public void setDescriptionAr(String v) {
        this.descriptionAr = v;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean v) {
        this.isActive = v;
    }

    public List<SignQuestion> getQuestions() {
        return questions;
    }

    public List<SignExam> getExams() {
        return exams;
    }
}
