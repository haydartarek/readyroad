package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import com.readyroad.readyroadbackend.util.TextNormalizer;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A question linked to a specific {@link RoadSign}.
 *
 * Choice-count invariant (enforced by the importer and delivery layer):
 *   HARD questions  → exactly 2 choices
 *   EASY/MEDIUM     → up to 3 choices
 */
@Entity
@Table(name = "sign_questions")
public class SignQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sign_id", nullable = false)
    private RoadSign sign;

    /** Unique reference key, e.g. "A1_Q01" or "onderbord_giii_aquaplaning_Q01". Used for upsert matching. */
    @Column(name = "question_ref", nullable = false, length = 100)
    private String questionRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 20)
    private SignQuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SignDifficulty difficulty;

    @Column(name = "is_critical", nullable = false)
    private Boolean isCritical = false;

    @Column(name = "show_sign", nullable = false)
    private Boolean showSign = true;

    // ── Multilingual question text ────────────────────────────────────────────
    @Column(name = "question_nl", columnDefinition = "TEXT")
    private String questionNl;

    @Column(name = "question_en", columnDefinition = "TEXT")
    private String questionEn;

    @Column(name = "question_fr", columnDefinition = "TEXT")
    private String questionFr;

    @Column(name = "question_ar", columnDefinition = "TEXT")
    private String questionAr;

    // ── Multilingual explanation ──────────────────────────────────────────────
    @Column(name = "explanation_nl", columnDefinition = "TEXT")
    private String explanationNl;

    @Column(name = "explanation_en", columnDefinition = "TEXT")
    private String explanationEn;

    @Column(name = "explanation_fr", columnDefinition = "TEXT")
    private String explanationFr;

    @Column(name = "explanation_ar", columnDefinition = "TEXT")
    private String explanationAr;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<SignChoice> choices = new ArrayList<>();

    // ── Helpers ───────────────────────────────────────────────────────────────
    public void addChoice(SignChoice choice) {
        choice.setQuestion(this);
        choices.add(choice);
    }

    public void clearChoices() {
        choices.forEach(c -> c.setQuestion(null));
        choices.clear();
    }

    @Transient
    public int getExpectedChoiceCount() {
        return difficulty == SignDifficulty.HARD || questionType == SignQuestionType.IS_IT_ALLOWED ? 2 : 3;
    }

    @Transient
    public List<SignChoice> getDeliverableChoices() {
        if (choices == null || choices.isEmpty()) {
            return List.of();
        }
        return choices.stream()
                .sorted(Comparator.comparing(
                        SignChoice::getDisplayOrder,
                        Comparator.nullsLast(Integer::compareTo)))
                .limit(getExpectedChoiceCount())
                .toList();
    }

    // ── Text normalisation ────────────────────────────────────────────────────
    @Override
    protected void normalizeTextFields() {
        questionNl    = TextNormalizer.normalize(questionNl);
        questionEn    = TextNormalizer.normalize(questionEn);
        questionFr    = TextNormalizer.normalize(questionFr);
        questionAr    = TextNormalizer.normalize(questionAr);
        explanationNl = TextNormalizer.normalize(explanationNl);
        explanationEn = TextNormalizer.normalize(explanationEn);
        explanationFr = TextNormalizer.normalize(explanationFr);
        explanationAr = TextNormalizer.normalize(explanationAr);
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public RoadSign getSign()                       { return sign; }
    public void     setSign(RoadSign v)             { this.sign = v; }

    public String getQuestionRef()                  { return questionRef; }
    public void   setQuestionRef(String v)          { this.questionRef = v; }

    public SignQuestionType getQuestionType()        { return questionType; }
    public void             setQuestionType(SignQuestionType v) { this.questionType = v; }

    public SignDifficulty getDifficulty()            { return difficulty; }
    public void           setDifficulty(SignDifficulty v) { this.difficulty = v; }

    public Boolean getIsCritical()                  { return isCritical; }
    public void    setIsCritical(Boolean v)         { this.isCritical = v; }

    public Boolean getShowSign()                    { return showSign; }
    public void    setShowSign(Boolean v)           { this.showSign = v; }

    public String getQuestionNl()                   { return questionNl; }
    public void   setQuestionNl(String v)           { this.questionNl = v; }

    public String getQuestionEn()                   { return questionEn; }
    public void   setQuestionEn(String v)           { this.questionEn = v; }

    public String getQuestionFr()                   { return questionFr; }
    public void   setQuestionFr(String v)           { this.questionFr = v; }

    public String getQuestionAr()                   { return questionAr; }
    public void   setQuestionAr(String v)           { this.questionAr = v; }

    public String getExplanationNl()                { return explanationNl; }
    public void   setExplanationNl(String v)        { this.explanationNl = v; }

    public String getExplanationEn()                { return explanationEn; }
    public void   setExplanationEn(String v)        { this.explanationEn = v; }

    public String getExplanationFr()                { return explanationFr; }
    public void   setExplanationFr(String v)        { this.explanationFr = v; }

    public String getExplanationAr()                { return explanationAr; }
    public void   setExplanationAr(String v)        { this.explanationAr = v; }

    public Boolean getIsActive()                    { return isActive; }
    public void    setIsActive(Boolean v)           { this.isActive = v; }

    public List<SignChoice> getChoices()            { return choices; }
}
