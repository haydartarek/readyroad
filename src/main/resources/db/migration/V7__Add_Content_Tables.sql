-- V7__Add_Content_Tables.sql
-- إضافة جداول المحتوى الإضافي (بدون تعارض مع V11)
-- Traffic Rules, Sign Details (NO quiz_questions - handled in V3)
-- Updated: 2026-01-15 - Removed conflicting tables

-- ========================================
-- جدول القواعد والتعليمات المرورية
-- Traffic Rules and Instructions Table
-- ========================================

CREATE TABLE traffic_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_code VARCHAR(20) NOT NULL UNIQUE,
    title_ar TEXT NOT NULL,
    title_en TEXT NOT NULL,
    title_nl TEXT NOT NULL,
    title_fr TEXT NOT NULL,
    content_ar TEXT,
    content_en TEXT,
    content_nl TEXT,
    content_fr TEXT,
    category VARCHAR(50),
    importance_level ENUM('HIGH', 'MEDIUM', 'LOW') DEFAULT 'MEDIUM',
    applies_to VARCHAR(100), -- 'ALL', 'CAR', 'MOTORCYCLE', 'BICYCLE', etc.
    penalty_info_ar TEXT,
    penalty_info_en TEXT,
    penalty_info_nl TEXT,
    penalty_info_fr TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    INDEX idx_category (category),
    INDEX idx_importance (importance_level),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- جدول ربط القواعد بالعلامات المرورية
-- Traffic Sign Rules Mapping Table
-- ========================================

CREATE TABLE traffic_sign_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    traffic_sign_id BIGINT NOT NULL,
    traffic_rule_id BIGINT NOT NULL,
    relationship_type ENUM('REQUIRED', 'RELATED', 'EXCEPTION') DEFAULT 'RELATED',
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id) ON DELETE CASCADE,
    FOREIGN KEY (traffic_rule_id) REFERENCES traffic_rules(id) ON DELETE CASCADE,
    INDEX idx_sign (traffic_sign_id),
    INDEX idx_rule (traffic_rule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- NOTE: quiz_questions و quiz_answer_options محذوفة
-- السبب: النظام يستخدم exam_questions و practice_questions (من V3)
-- هيكل الأسئلة: option1/option2/option3 + correct_answer (integer)
-- للمزيد: راجع V3__Create_Learning_System_Tables.sql
-- ========================================

-- ========================================
-- جدول الأوصاف المفصلة للعلامات
-- Traffic Sign Detailed Descriptions Table
-- ========================================

CREATE TABLE traffic_sign_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    traffic_sign_id BIGINT NOT NULL UNIQUE,
    detailed_description_ar TEXT,
    detailed_description_en TEXT,
    detailed_description_nl TEXT,
    detailed_description_fr TEXT,
    when_to_use_ar TEXT,
    when_to_use_en TEXT,
    when_to_use_nl TEXT,
    when_to_use_fr TEXT,
    common_mistakes_ar TEXT,
    common_mistakes_en TEXT,
    common_mistakes_nl TEXT,
    common_mistakes_fr TEXT,
    tips_ar TEXT,
    tips_en TEXT,
    tips_nl TEXT,
    tips_fr TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- NOTE: user_quiz_results محذوفة
-- السبب: مستبدلة بنظام ذكي في V11__Smart_Quiz_System.sql
-- الجداول الجديدة:
-- - quiz_attempts (المحاولات)
-- - quiz_user_answers (الإجابات التفصيلية)
-- - user_question_history (التاريخ والإحصائيات)
-- - user_error_patterns (تحليل الأخطاء)
-- ========================================

-- ========================================
-- ⚠️ CRITICAL ARCHITECTURAL NOTE:
-- ========================================
-- هذا الملف كان يحتوي على:
--   • quiz_questions (محذوف - استبدل بـ exam_questions في V3)
--   • quiz_answer_options (محذوف - استبدل بـ option1/option2/option3 في V3)
--   • user_quiz_results (محذوف - استبدل بنظام V11 الذكي)
--
-- 🎯 النظام الفعلي المُستخدم الآن:
--   ✅ exam_questions (V3): للامتحانات - 2-3 خيارات فقط
--   ✅ practice_questions (V3): للتدريب - 4 خيارات
--   ✅ option1_*, option2_*, option3_*, option4_* (أعمدة مباشرة)
--   ✅ correct_answer INT (1, 2, 3)
--
-- 🛡️ الحماية من 4 خيارات في الامتحانات:
--   📍 V16__Add_Exam_Question_Validation_Triggers.sql
--      • trg_exam_question_option_limit_insert
--      • trg_exam_question_option_limit_update
--      • يمنع option4 تمامًا في exam_questions
--      • يفرض 2-3 خيارات فقط
--      • يتحقق من correct_answer ≤ عدد الخيارات
--
-- 📚 للمزيد:
--   • SQL_GENERATOR_RULES.md (قواعد إنشاء الأسئلة)
--   • SMART_QUIZ_ARCHITECTURE.md (معمارية النظام)
--   • V3__Create_Learning_System_Tables.sql (الجداول الفعلية)
-- ========================================