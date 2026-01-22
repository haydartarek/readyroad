package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ═══════════════════════════════════════════════════════════════════
 *  أنماط الأخطاء (User Error Patterns)
 * ═══════════════════════════════════════════════════════════════════
 * 
 * 🎯 جوهر القانون الرابع: الإحصائيات العامة
 * 
 * هذا الـ Entity يسجل أنماط الأخطاء، ليس الأخطاء نفسها:
 * 
 * • ErrorType → تصنيف عام (SIGN_CONFUSION, PRIORITY_MISUNDERSTANDING...)
 * • لا يوجد حقل "اسم الإشارة" أو "رقم القانون"
 * • النمط قابل للتطبيق على أي محتوى
 * 
 * مثال التطبيق:
 * - في الإشارات: SIGN_CONFUSION = خلط بين علامتين
 * - في الطب: SIGN_CONFUSION = خلط بين أعراض
 * - في الرياضيات: SIGN_CONFUSION = خلط بين قوانين
 * 
 * القاعدة: النمط عام، المحتوى متغير
 * 
 * ⚠️ هذا هو الجهل المتعمّد (القانون الخامس)
 * 
 * @see SYSTEM_LAWS.md - القانون الرابع والخامس
 */
@Entity
@Table(name = "user_error_patterns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserErrorPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type", nullable = false)
    private ErrorType errorType;

    // NOTE: Temporarily disabled - QuizQuestion entity is not active
    // We use user_error_patterns table from V11 migration instead
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "question_id", nullable = false)
    // private QuizQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traffic_sign_id")
    private TrafficSign trafficSign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum ErrorType {
        SIGN_CONFUSION,              // خلط بين علامات متشابهة
        SUPPLEMENTARY_IGNORED,       // تجاهل لوحة تكميلية
        PRIORITY_MISUNDERSTANDING,   // فهم خاطئ للأولوية
        SPEED_LIMIT_ERROR,           // خطأ في حدود السرعة
        ZONE_CONFUSION,              // خلط بين المناطق
        RULE_OVERGENERALIZATION,     // تعميم قاعدة في مكان خاطئ
        OTHER
    }

    @PrePersist
    protected void onCreate() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}
