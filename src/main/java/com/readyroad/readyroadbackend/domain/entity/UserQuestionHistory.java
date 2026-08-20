package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Tracks when users see questions to enforce 24h cooldown (Law #1).
 * Enhanced in Phase 4 to track performance for adaptive difficulty (Law #2).
 * Generic design - works for any content domain.
 * 
 * Phase B Enhancement: Auto-fill question_ref_id using @PrePersist
 */
@Entity
@Table(name = "user_question_history", indexes = {
        @Index(name = "idx_user_question_history_user_answered", columnList = "user_id,answered_at"),
        @Index(name = "idx_user_question_history_question_answered", columnList = "question_id,answered_at"),
        @Index(name = "idx_user_question_history_lookup", columnList = "user_id,question_id,answered_at"),
        @Index(name = "idx_user_question_history_perf", columnList = "user_id,answered_at,is_correct")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_question_ref", columnNames = { "user_id", "question_ref_id" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UserQuestionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    /**
     * Reference ID for tracking unique question instances
     * Auto-filled from question_id via @PrePersist if not set
     */
    @Column(name = "question_ref_id", nullable = false)
    private Long questionRefId;

    @Column(name = "question_type", length = 10)
    private String questionType;

    /** Timestamp of the latest completed answer event. */
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    /**
     * Phase 4: Performance tracking for adaptive difficulty (Law #2)
     * NULL = question shown but not answered yet (cooldown only)
     * TRUE/FALSE = answer correctness (enables adaptive difficulty)
     */
    @Column(name = "is_correct")
    private Boolean isCorrect;

    /**
     * Phase 4: Time taken to answer (seconds)
     * Used for advanced performance analysis
     * NULL = not tracked or not answered
     */
    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    /** Latest verified presentation time. Historical rows are intentionally not inferred. */
    @Column(name = "last_presented_at")
    private LocalDateTime lastPresentedAt;

    /** Verified presentation count recorded after Phase 3A. */
    @Column(name = "times_presented", nullable = false)
    @Builder.Default
    private Integer timesPresented = 0;

    /**
     * Legacy compatibility timestamp. New selection logic must use lastPresentedAt.
     */
    @Column(name = "last_shown_at")
    private LocalDateTime lastShownAt;

    /**
     * Tracks the context in which question was shown
     * Values: RANDOM, CATEGORY, EXAM, SMART_QUIZ
     */
    @Column(name = "last_shown_type", length = 20)
    private String lastShownType;

    /**
     * Number of times this question was shown to user
     */
    @Column(name = "times_shown", nullable = false)
    @Builder.Default
    private Integer timesShown = 0;

    /**
     * Number of times user answered correctly
     */
    @Column(name = "times_correct", nullable = false)
    @Builder.Default
    private Integer timesCorrect = 0;

    /**
     * Number of times user answered incorrectly
     */
    @Column(name = "times_wrong", nullable = false)
    @Builder.Default
    private Integer timesIncorrect = 0;

    /**
     * Result of the last answer (null if not answered yet)
     */
    // @Column(name = "last_answer_correct")
    private Boolean lastAnswerCorrect;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Optional relationships (LAZY to avoid loading unless needed)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private QuizQuestion question;

    // ═══════════════════════════════════════════════════════════════
    // ✅ JPA LIFECYCLE HOOKS - Option B Implementation
    // ═══════════════════════════════════════════════════════════════

    /**
     * Executed before inserting a new record into the database.
     * Auto-fills question_ref_id from question_id if not set.
     * Initializes timestamps and counters.
     */
    @PrePersist
    protected void onCreate() {
        log.debug("@PrePersist triggered for UserQuestionHistory");

        // ✅ Auto-fill question_ref_id from question_id
        if (this.questionRefId == null && this.questionId != null) {
            log.info("Auto-filling question_ref_id from question_id: {}", this.questionId);
            this.questionRefId = this.questionId;
        }

        // ✅ Validate required fields
        if (this.questionRefId == null) {
            log.error("Cannot persist UserQuestionHistory: question_ref_id is null and question_id is null");
            throw new IllegalStateException("question_ref_id cannot be null (and question_id is also null)");
        }

        // ✅ Set timestamps
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        // answeredAt is set only when the user submits an answer.

        // ✅ Initialize counters
        if (this.timesShown == null) {
            this.timesShown = 0;
        }
        if (this.timesPresented == null) {
            this.timesPresented = 0;
        }
        if (this.timesCorrect == null) {
            this.timesCorrect = 0;
        }
        if (this.timesIncorrect == null) {
            this.timesIncorrect = 0;
        }

        log.debug("onCreate complete - userId: {}, questionId: {}, questionRefId: {}, createdAt: {}",
                this.userId, this.questionId, this.questionRefId, this.createdAt);
    }

    /**
     * Executed before updating an existing record.
     * Ensures question_ref_id is never null and updates timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        log.debug("@PreUpdate triggered for UserQuestionHistory id: {}", this.id);

        // ✅ Ensure question_ref_id is never null during update
        if (this.questionRefId == null && this.questionId != null) {
            log.warn("question_ref_id was null during update for id: {}. Auto-filling from question_id: {}",
                    this.id, this.questionId);
            this.questionRefId = this.questionId;
        }

        // ✅ Validate
        if (this.questionRefId == null) {
            log.error("Cannot update UserQuestionHistory id: {}. question_ref_id is null", this.id);
            throw new IllegalStateException("question_ref_id cannot be null during update");
        }

        // ✅ Update timestamp
        this.updatedAt = LocalDateTime.now();

        log.debug("onUpdate complete - id: {}, questionRefId: {}, updatedAt: {}",
                this.id, this.questionRefId, this.updatedAt);
    }

    // ═══════════════════════════════════════════════════════════════
    // 📊 BUSINESS LOGIC METHODS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Records that the question was shown to the user
     */
    public void recordShown() {
        this.timesShown = this.timesShown == null ? 1 : this.timesShown + 1;
        this.timesPresented = this.timesPresented == null ? 1 : this.timesPresented + 1;
        LocalDateTime now = LocalDateTime.now();
        this.lastShownAt = now;
        this.lastPresentedAt = now;
        log.debug("Question {} shown to user {} - Times shown: {}",
                this.questionId, this.userId, this.timesShown);
    }

    public LocalDateTime getLastAnsweredAt() {
        return answeredAt;
    }

    public int getTimesAnswered() {
        int correct = timesCorrect == null ? 0 : timesCorrect;
        int incorrect = timesIncorrect == null ? 0 : timesIncorrect;
        return correct + incorrect;
    }

    /**
     * Records a correct answer
     * 
     * @param timeTaken time taken in seconds (optional)
     */
    public void recordCorrectAnswer(Integer timeTaken) {
        this.timesCorrect++;
        this.lastAnswerCorrect = true;
        this.isCorrect = true;
        this.answeredAt = LocalDateTime.now();
        if (timeTaken != null) {
            this.timeTakenSeconds = timeTaken;
        }
        log.debug("Correct answer recorded for question {} by user {} - Total correct: {}",
                this.questionId, this.userId, this.timesCorrect);
    }

    /**
     * Records an incorrect answer
     * 
     * @param timeTaken time taken in seconds (optional)
     */
    public void recordIncorrectAnswer(Integer timeTaken) {
        this.timesIncorrect++;
        this.lastAnswerCorrect = false;
        this.isCorrect = false;
        this.answeredAt = LocalDateTime.now();
        if (timeTaken != null) {
            this.timeTakenSeconds = timeTaken;
        }
        log.debug("Incorrect answer recorded for question {} by user {} - Total incorrect: {}",
                this.questionId, this.userId, this.timesIncorrect);
    }

    /**
     * Calculates success rate as a percentage
     * 
     * @return success rate (0-100) or 0 if no attempts
     */
    public double getSuccessRate() {
        int totalAttempts = this.timesCorrect + this.timesIncorrect;
        if (totalAttempts == 0) {
            return 0.0;
        }
        return (double) this.timesCorrect / totalAttempts * 100.0;
    }

    /**
     * Checks if the question is within 24-hour cooldown period
     * 
     * @return true if question was answered in last 24 hours
     */
    public boolean isWithinCooldown() {
        if (this.answeredAt == null) {
            return false;
        }
        LocalDateTime cooldownEnd = this.answeredAt.plusHours(24);
        return LocalDateTime.now().isBefore(cooldownEnd);
    }

    /**
     * Gets hours until cooldown expires
     * 
     * @return hours remaining (0 if cooldown expired)
     */
    public long getHoursUntilCooldownExpires() {
        if (!isWithinCooldown()) {
            return 0;
        }
        LocalDateTime cooldownEnd = this.answeredAt.plusHours(24);
        return java.time.Duration.between(LocalDateTime.now(), cooldownEnd).toHours();
    }

    /**
     * Determines if user needs more practice on this question
     * 
     * @return true if success rate < 70% or answered incorrectly recently
     */
    public boolean needsMorePractice() {
        // If never attempted correctly
        if (this.timesCorrect == 0 && this.timesIncorrect > 0) {
            return true;
        }

        // If success rate is low
        if (getSuccessRate() < 70.0) {
            return true;
        }

        // If last answer was incorrect
        if (Boolean.FALSE.equals(this.lastAnswerCorrect)) {
            return true;
        }

        return false;
    }

    /**
     * Gets difficulty level suggestion based on performance
     * 
     * @return "EASY", "MEDIUM", or "HARD"
     */
    public String getSuggestedDifficulty() {
        double successRate = getSuccessRate();

        if (successRate >= 80.0) {
            return "HARD";
        } else if (successRate >= 50.0) {
            return "MEDIUM";
        } else {
            return "EASY";
        }
    }
}
