package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ═══════════════════════════════════════════════════════════════════
 *  نتيجة السؤال (Question Result DTO)
 * ═══════════════════════════════════════════════════════════════════
 * 
 * 🎯 يطبق القانون الثاني: التصحيح المقيَّد
 * 🎯 يطبق القانون الثالث: عرض النتائج
 * 
 * البنية:
 * • isCorrect → Boolean (true=أخضر ✅, false=أحمر ❌)
 * • error_explanation_* → context-specific من السؤال
 * 
 * القاعدة:
 * - لا يوجد تصحيح عام هنا
 * - كل سؤال يأتي بتفسيره من قاعدة البيانات
 * - Frontend يعرض isCorrect كلون مباشرة
 * 
 * @see SYSTEM_LAWS.md - القانون الثاني والثالث
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDTO {
    private Long questionId;
    private Long selectedOptionId;
    private Long correctOptionId;
    private Boolean isCorrect; // ← القانون الثالث: أخضر (true) أو أحمر (false)
    
    // ← القانون الثاني: شرح مقيَّد من السؤال نفسه (context-specific)
    private String errorExplanationAr;
    private String errorExplanationEn;
    private String errorExplanationNl;
    private String errorExplanationFr;
    private String errorType;
    
    // شرح الإجابة الصحيحة
    private String correctExplanationAr;
    private String correctExplanationEn;
    private String correctExplanationNl;
    private String correctExplanationFr;
}
