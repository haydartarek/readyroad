package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ═══════════════════════════════════════════════════════════════════
 *  سجل تاريخ الأسئلة للمستخدم (User Question History)
 * ═══════════════════════════════════════════════════════════════════
 * 
 * 🎯 جوهر القانون الأول: عدم التكرار
 * 
 * هذا الـ Entity هو القلب النابض لقانون الـ 24 ساعة:
 * 
 * • last_shown_at → متى ظهر السؤال آخر مرة
 * • times_shown → كم مرة ظهر (إحصائية)
 * • times_correct/wrong → أداء المستخدم (إحصائية عامة)
 * 
 * القاعدة الذهبية:
 * إذا كان (now - last_shown_at) < 24h → لا يظهر السؤال
 * 
 * ⚠️ هذا لا يعرف ماهية السؤال، فقط يسجل الوقت
 * 
 * NOTE: Temporarily disabled - references disabled QuizQuestion entity
 * We use user_question_history table from V11 migration instead
 *
 * @see SYSTEM_LAWS.md - القانون الأول
 */
// @Entity
// @Table(name = "user_question_history",
//        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "question_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserQuestionHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "last_shown_at", nullable = false)
    private LocalDateTime lastShownAt;

    @Column(name = "times_shown", nullable = false)
    private Integer timesShown = 1;

    @Column(name = "times_correct", nullable = false)
    private Integer timesCorrect = 0;

    @Column(name = "times_wrong", nullable = false)
    private Integer timesWrong = 0;

    public void recordShown() {
        this.lastShownAt = LocalDateTime.now();
        this.timesShown++;
    }

    public void recordAnswer(boolean isCorrect) {
        if (isCorrect) {
            this.timesCorrect++;
        } else {
            this.timesWrong++;
        }
    }

    public double getAccuracy() {
        int total = timesCorrect + timesWrong;
        if (total == 0) return 0.0;
        return (double) timesCorrect / total * 100.0;
    }
}
