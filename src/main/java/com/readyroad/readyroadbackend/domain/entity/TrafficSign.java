package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.util.TextNormalizer;
import jakarta.persistence.*;

@Entity
@Table(name = "traffic_signs")
public class TrafficSign extends BaseEntity {

    /**
     * Computed governance flag: true only when all 4 long_description fields are
     * present and non-empty.
     * Not persisted â€” derived at read time.
     */
    @Transient
    public boolean isLongDescriptionComplete() {
        return isPresent(longDescriptionEn)
                && isPresent(longDescriptionNl)
                && isPresent(longDescriptionFr)
                && isPresent(longDescriptionAr);
    }

    private static boolean isPresent(String s) {
        return s != null && !s.isBlank();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 50)
    private String signCode;

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

    @Column(columnDefinition = "TEXT")
    private String longDescriptionEn;

    @Column(columnDefinition = "TEXT")
    private String longDescriptionNl;

    @Column(columnDefinition = "TEXT")
    private String longDescriptionFr;

    @Column(columnDefinition = "TEXT")
    private String longDescriptionAr;

    @Column(name = "normalized_sign_code", length = 100)
    private String normalizedSignCode;

    @Column(length = 500)
    private String imageUrl;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Getters and Setters
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSignCode() {
        return signCode;
    }

    public void setSignCode(String signCode) {
        this.signCode = signCode;
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

    public String getLongDescriptionEn() {
        return longDescriptionEn;
    }

    public void setLongDescriptionEn(String longDescriptionEn) {
        this.longDescriptionEn = longDescriptionEn;
    }

    public String getLongDescriptionNl() {
        return longDescriptionNl;
    }

    public void setLongDescriptionNl(String longDescriptionNl) {
        this.longDescriptionNl = longDescriptionNl;
    }

    public String getLongDescriptionFr() {
        return longDescriptionFr;
    }

    public void setLongDescriptionFr(String longDescriptionFr) {
        this.longDescriptionFr = longDescriptionFr;
    }

    public String getLongDescriptionAr() {
        return longDescriptionAr;
    }

    public void setLongDescriptionAr(String longDescriptionAr) {
        this.longDescriptionAr = longDescriptionAr;
    }

    public String getNormalizedSignCode() {
        return normalizedSignCode;
    }

    public void setNormalizedSignCode(String normalizedSignCode) {
        this.normalizedSignCode = normalizedSignCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    protected void normalizeTextFields() {
        nameAr = TextNormalizer.normalize(nameAr);
        nameEn = TextNormalizer.normalize(nameEn);
        nameNl = TextNormalizer.normalize(nameNl);
        nameFr = TextNormalizer.normalize(nameFr);
        descriptionAr = TextNormalizer.normalize(descriptionAr);
        descriptionEn = TextNormalizer.normalize(descriptionEn);
        descriptionNl = TextNormalizer.normalize(descriptionNl);
        descriptionFr = TextNormalizer.normalize(descriptionFr);
        longDescriptionAr = TextNormalizer.normalize(longDescriptionAr);
        longDescriptionEn = TextNormalizer.normalize(longDescriptionEn);
        longDescriptionNl = TextNormalizer.normalize(longDescriptionNl);
        longDescriptionFr = TextNormalizer.normalize(longDescriptionFr);
    }
}
