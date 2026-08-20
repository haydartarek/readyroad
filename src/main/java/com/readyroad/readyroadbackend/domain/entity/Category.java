package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.util.TextNormalizer;
import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String code; // A, B, C, D, E, F, G, Z, M

    @Column(nullable = false, columnDefinition = "TEXT")
    private String nameAr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String nameEn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String nameNl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String nameFr;

    @Column(columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(columnDefinition = "TEXT")
    private String descriptionNl;

    @Column(columnDefinition = "TEXT")
    private String descriptionFr;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(name = "exam_target_weight")
    private Integer examTargetWeight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoryContentScope contentScope = CategoryContentScope.TRAFFIC_SIGN;

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameNl() {
        return nameNl;
    }

    public void setNameNl(String nameNl) {
        this.nameNl = nameNl;
    }

    public String getNameFr() {
        return nameFr;
    }

    public void setNameFr(String nameFr) {
        this.nameFr = nameFr;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public void setDescriptionAr(String descriptionAr) {
        this.descriptionAr = descriptionAr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionNl() {
        return descriptionNl;
    }

    public void setDescriptionNl(String descriptionNl) {
        this.descriptionNl = descriptionNl;
    }

    public String getDescriptionFr() {
        return descriptionFr;
    }

    public void setDescriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getExamTargetWeight() {
        return examTargetWeight;
    }

    public void setExamTargetWeight(Integer examTargetWeight) {
        this.examTargetWeight = examTargetWeight;
    }

    public CategoryContentScope getContentScope() {
        return contentScope;
    }

    public void setContentScope(CategoryContentScope contentScope) {
        this.contentScope = contentScope;
    }

    @Override
    protected void normalizeTextFields() {
        nameAr        = TextNormalizer.normalize(nameAr);
        nameEn        = TextNormalizer.normalize(nameEn);
        nameNl        = TextNormalizer.normalize(nameNl);
        nameFr        = TextNormalizer.normalize(nameFr);
        descriptionAr = TextNormalizer.normalize(descriptionAr);
        descriptionEn = TextNormalizer.normalize(descriptionEn);
        descriptionNl = TextNormalizer.normalize(descriptionNl);
        descriptionFr = TextNormalizer.normalize(descriptionFr);
    }
}

