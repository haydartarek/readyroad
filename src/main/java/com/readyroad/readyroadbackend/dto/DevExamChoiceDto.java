package com.readyroad.readyroadbackend.dto;

public class DevExamChoiceDto {

    private Long id;
    private Integer sortOrder;
    private String text; // resolved to requested language

    // ─── Constructors ────────────────────────────────────────────────────────

    public DevExamChoiceDto() {
    }

    // ─── Getters / Setters ──────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
