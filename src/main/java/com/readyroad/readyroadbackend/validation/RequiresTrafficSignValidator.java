package com.readyroad.readyroadbackend.validation;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for Traffic Sign Requirement - Story D3
 *
 * Ensures quiz questions comply with Belgian law by having traffic sign context.
 * Questions without traffic signs cannot be used in exams as they lack legal context.
 */
public class RequiresTrafficSignValidator
    implements ConstraintValidator<RequiresTrafficSign, QuizQuestion> {

    @Override
    public boolean isValid(QuizQuestion question, ConstraintValidatorContext context) {
        // Null is valid (will be caught by @NotNull if needed)
        if (question == null) {
            return true;
        }

        // Check if traffic sign is present
        if (question.getTrafficSign() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Question must reference a traffic sign to provide legal context for Belgian exams"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
