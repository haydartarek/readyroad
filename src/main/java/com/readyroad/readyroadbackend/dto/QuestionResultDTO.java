package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ═══════════════════════════════════════════════════════════════════
 *  Question Result (Question Result DTO)
 * ═══════════════════════════════════════════════════════════════════
 * 
 * Applies Law Two: Restricted Correction
 * Applies Law Three: Results Display
 * 
 * Structure:
 * • isCorrect -> Boolean (true=green, false=red)
 * • error_explanation_* -> context-specific from the question
 * 
 * Rule:
 * - There is no general correction here
 * - Each question brings its explanation from the database
 * - Frontend displays isCorrect as a color directly
 * 
 * @see SYSTEM_LAWS.md - Laws Two and Three
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDTO {
    private Long questionId;
    private Long selectedOptionId;
    private Long correctOptionId;
    private Boolean isCorrect; // <- Law Three: green (true) or red (false)
    
    // <- Law Two: restricted explanation from the question itself (context-specific)
    private String errorExplanationAr;
    private String errorExplanationEn;
    private String errorExplanationNl;
    private String errorExplanationFr;
    private String errorType;
    
    // Correct answer explanation
    private String correctExplanationAr;
    private String correctExplanationEn;
    private String correctExplanationNl;
    private String correctExplanationFr;
}
