package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.util.TextNormalizer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * One answer choice for a {@link SignQuestion}.
 * Does not extend BaseEntity (no updatedAt needed for immutable choices).
 */
@Entity
@Table(name = "sign_choices")
public class SignChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private SignQuestion question;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    // ── Multilingual choice text ──────────────────────────────────────────────
    @Column(name = "text_nl", columnDefinition = "TEXT")
    private String textNl;

    @Column(name = "text_en", columnDefinition = "TEXT")
    private String textEn;

    @Column(name = "text_fr", columnDefinition = "TEXT")
    private String textFr;

    @Column(name = "text_ar", columnDefinition = "TEXT")
    private String textAr;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        textNl = TextNormalizer.normalize(textNl);
        textEn = TextNormalizer.normalize(textEn);
        textFr = TextNormalizer.normalize(textFr);
        textAr = TextNormalizer.normalize(textAr);
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                        { return id; }

    public SignQuestion getQuestion()          { return question; }
    public void         setQuestion(SignQuestion v) { this.question = v; }

    public Integer getDisplayOrder()           { return displayOrder; }
    public void    setDisplayOrder(Integer v)  { this.displayOrder = v; }

    public Boolean getIsCorrect()              { return isCorrect; }
    public void    setIsCorrect(Boolean v)     { this.isCorrect = v; }

    public String getTextNl()                  { return textNl; }
    public void   setTextNl(String v)          { this.textNl = v; }

    public String getTextEn()                  { return textEn; }
    public void   setTextEn(String v)          { this.textEn = v; }

    public String getTextFr()                  { return textFr; }
    public void   setTextFr(String v)          { this.textFr = v; }

    public String getTextAr()                  { return textAr; }
    public void   setTextAr(String v)          { this.textAr = v; }

    public LocalDateTime getCreatedAt()        { return createdAt; }
}
