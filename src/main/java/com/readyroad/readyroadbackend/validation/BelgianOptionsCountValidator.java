package com.readyroad.readyroadbackend.validation;

import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

/**
 * Validator for Belgian Options Count - Story D1
 *
 * Ensures quiz questions comply with Belgian rules:
 * - Minimum 2 options
 * - Maximum 3 options
 */
public class BelgianOptionsCountValidator
        implements ConstraintValidator<BelgianOptionsCount, List<QuizAnswerOption>> {

    private static final int MIN_OPTIONS = 2;
    private static final int MAX_OPTIONS = 3;

    @Override
    public boolean isValid(List<QuizAnswerOption> options,
            ConstraintValidatorContext context) {
        // Null is valid (will be caught by @NotNull if needed)
        if (options == null) {
            return true;
        }

        int count = (int) options.stream()
                .filter(option -> !Boolean.FALSE.equals(option.getIsActive()))
                .count();

        if (count < MIN_OPTIONS || count > MAX_OPTIONS) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Belgian standard requires %d-%d options. Found: %d",
                            MIN_OPTIONS, MAX_OPTIONS, count))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
