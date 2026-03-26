package com.readyroad.readyroadbackend.dto;

import java.util.List;

public class DevExamCategoryDto {

    private Long id;
    private String slug;
    private String name; // resolved to requested language
    private String description; // resolved to requested language
    private Integer timeLimitMinutes;
    private Integer passingScorePercent;
    private List<String> difficulties; // e.g. ["BEGINNER","INTERMEDIATE","ADVANCED"]

    // ─── Constructors ────────────────────────────────────────────────────────

    public DevExamCategoryDto() {
    }

    // ─── Getters / Setters ──────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public Integer getPassingScorePercent() {
        return passingScorePercent;
    }

    public void setPassingScorePercent(Integer passingScorePercent) {
        this.passingScorePercent = passingScorePercent;
    }

    public List<String> getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(List<String> difficulties) {
        this.difficulties = difficulties;
    }
}
