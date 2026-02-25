package com.readyroad.readyroadbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Belgian Compliance Validation - Story D1
 *
 * Validates that quiz questions have 2-3 answer options.
 * This is a legal requirement for Belgian driving license exams.
 *
 * Usage:
 * 
 * @BelgianOptionsCount
 *                      private List<QuizAnswerOption> options;
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BelgianOptionsCountValidator.class)
public @interface BelgianOptionsCount {

    String message() default "Belgian standard requires 2-3 options";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
