package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.domain.converter.StringListJsonConverter;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
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

    @Column(name = "summary_nl", columnDefinition = "LONGTEXT")
    private String summaryNl;

    @Column(name = "summary_en", columnDefinition = "LONGTEXT")
    private String summaryEn;

    @Column(name = "summary_fr", columnDefinition = "LONGTEXT")
    private String summaryFr;

    @Column(name = "summary_ar", columnDefinition = "LONGTEXT")
    private String summaryAr;

    @Column(name = "driver_guidance_nl", columnDefinition = "LONGTEXT")
    private String driverGuidanceNl;

    @Column(name = "driver_guidance_en", columnDefinition = "LONGTEXT")
    private String driverGuidanceEn;

    @Column(name = "driver_guidance_fr", columnDefinition = "LONGTEXT")
    private String driverGuidanceFr;

    @Column(name = "driver_guidance_ar", columnDefinition = "LONGTEXT")
    private String driverGuidanceAr;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "exceptions_nl", columnDefinition = "LONGTEXT")
    private List<String> exceptionsNl = new ArrayList<>();

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "exceptions_en", columnDefinition = "LONGTEXT")
    private List<String> exceptionsEn = new ArrayList<>();

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "exceptions_fr", columnDefinition = "LONGTEXT")
    private List<String> exceptionsFr = new ArrayList<>();

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "exceptions_ar", columnDefinition = "LONGTEXT")
    private List<String> exceptionsAr = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // ── Relationships ────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "sign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SignQuestion> questions = new ArrayList<>();

    @OneToMany(mappedBy = "sign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SignExam> exams = new ArrayList<>();

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

    public String getSummaryNl() {
        return summaryNl;
    }

    public void setSummaryNl(String v) {
        this.summaryNl = v;
    }

    public String getSummaryEn() {
        return summaryEn;
    }

    public void setSummaryEn(String v) {
        this.summaryEn = v;
    }

    public String getSummaryFr() {
        return summaryFr;
    }

    public void setSummaryFr(String v) {
        this.summaryFr = v;
    }

    public String getSummaryAr() {
        return summaryAr;
    }

    public void setSummaryAr(String v) {
        this.summaryAr = v;
    }

    public String getDriverGuidanceNl() {
        return driverGuidanceNl;
    }

    public void setDriverGuidanceNl(String v) {
        this.driverGuidanceNl = v;
    }

    public String getDriverGuidanceEn() {
        return driverGuidanceEn;
    }

    public void setDriverGuidanceEn(String v) {
        this.driverGuidanceEn = v;
    }

    public String getDriverGuidanceFr() {
        return driverGuidanceFr;
    }

    public void setDriverGuidanceFr(String v) {
        this.driverGuidanceFr = v;
    }

    public String getDriverGuidanceAr() {
        return driverGuidanceAr;
    }

    public void setDriverGuidanceAr(String v) {
        this.driverGuidanceAr = v;
    }

    public List<String> getExceptionsNl() {
        return exceptionsNl;
    }

    public void setExceptionsNl(List<String> v) {
        this.exceptionsNl = v == null ? new ArrayList<>() : new ArrayList<>(v);
    }

    public List<String> getExceptionsEn() {
        return exceptionsEn;
    }

    public void setExceptionsEn(List<String> v) {
        this.exceptionsEn = v == null ? new ArrayList<>() : new ArrayList<>(v);
    }

    public List<String> getExceptionsFr() {
        return exceptionsFr;
    }

    public void setExceptionsFr(List<String> v) {
        this.exceptionsFr = v == null ? new ArrayList<>() : new ArrayList<>(v);
    }

    public List<String> getExceptionsAr() {
        return exceptionsAr;
    }

    public void setExceptionsAr(List<String> v) {
        this.exceptionsAr = v == null ? new ArrayList<>() : new ArrayList<>(v);
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
