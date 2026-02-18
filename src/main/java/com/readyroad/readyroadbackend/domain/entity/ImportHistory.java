package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Persists metadata for every preview / execute import operation.
 */
@Entity
@Table(name = "import_history")
public class ImportHistory extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    @Column(nullable = false, length = 50)
    private String importType;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(length = 64)
    private String fileChecksum;

    @Column(nullable = false)
    private Boolean dryRun;

    @Column(nullable = false)
    private Integer createdCount = 0;

    @Column(nullable = false)
    private Integer updatedCount = 0;

    @Column(nullable = false)
    private Integer skippedCount = 0;

    @Column(nullable = false, length = 20)
    private String status = "SUCCESS";

    @Column(columnDefinition = "TEXT")
    private String errorSummary;

    @Column(columnDefinition = "TEXT")
    private String warningSummary;

    // ── Getters & Setters ──

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileChecksum() {
        return fileChecksum;
    }

    public void setFileChecksum(String fileChecksum) {
        this.fileChecksum = fileChecksum;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    public Integer getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(Integer createdCount) {
        this.createdCount = createdCount;
    }

    public Integer getUpdatedCount() {
        return updatedCount;
    }

    public void setUpdatedCount(Integer updatedCount) {
        this.updatedCount = updatedCount;
    }

    public Integer getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorSummary() {
        return errorSummary;
    }

    public void setErrorSummary(String errorSummary) {
        this.errorSummary = errorSummary;
    }

    public String getWarningSummary() {
        return warningSummary;
    }

    public void setWarningSummary(String warningSummary) {
        this.warningSummary = warningSummary;
    }
}
