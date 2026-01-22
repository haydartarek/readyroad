package com.readyroad.readyroadbackend.validation;

/**
 * Belgian Compliance Validation Group - Story D4
 *
 * Used to trigger full Belgian compliance validation at publish time.
 * This group enforces:
 * - 2-3 answer options
 * - NL and FR translations
 * - Traffic sign context
 *
 * Usage:
 * validator.validate(question, PublishValidation.class)
 */
public interface PublishValidation {
    // Marker interface for validation groups
}
