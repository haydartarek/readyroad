package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dev_exam_settings", uniqueConstraints = @UniqueConstraint(columnNames = "category_id"))
public class DevExamSetting {

    @Id
    @Column(name = "category_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "category_id", nullable = false)
    private DevExamCategory category;

    @Column(name = "questions_beginner", nullable = false)
    private Integer questionsPerLevelBeginner = 3;

    @Column(name = "questions_intermediate", nullable = false)
    private Integer questionsPerLevelIntermediate = 3;

    @Column(name = "questions_advanced", nullable = false)
    private Integer questionsPerLevelAdvanced = 3;

    @Column(name = "time_limit_minutes", nullable = false)
    private Integer timeLimitMinutes = 20;

    @Column(name = "pass_score_percent", nullable = false)
    private Integer passingScorePercent = 70;

    // ─── Getters / Setters ──────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DevExamCategory getCategory() {
        return category;
    }

    public void setCategory(DevExamCategory category) {
        this.category = category;
    }

    public Integer getQuestionsPerLevelBeginner() {
        return questionsPerLevelBeginner;
    }

    public void setQuestionsPerLevelBeginner(Integer v) {
        this.questionsPerLevelBeginner = v;
    }

    public Integer getQuestionsPerLevelIntermediate() {
        return questionsPerLevelIntermediate;
    }

    public void setQuestionsPerLevelIntermediate(Integer v) {
        this.questionsPerLevelIntermediate = v;
    }

    public Integer getQuestionsPerLevelAdvanced() {
        return questionsPerLevelAdvanced;
    }

    public void setQuestionsPerLevelAdvanced(Integer v) {
        this.questionsPerLevelAdvanced = v;
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
}
