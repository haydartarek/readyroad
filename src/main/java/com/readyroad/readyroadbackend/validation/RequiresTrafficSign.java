package com.readyroad.readyroadbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Belgian Compliance Validation - Story D3
 *
 * Validates that exam questions have a traffic sign reference.
 * This ensures every exam question explains a real-world sign context.
 *
 * Usage:
 * @RequiresTrafficSign(groups = PublishValidation.class)
 * @Entity
 * public class QuizQuestion { ... }
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiresTrafficSignValidator.class)
public @interface RequiresTrafficSign {

    String message() default "Belgian compliance requires traffic sign context for exam questions";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
