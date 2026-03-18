package com.readyroad.readyroadbackend.domain.enums;

/**
 * Category for road signs in the Sign Quiz System.
 * Maps to the first letter/prefix of the Belgian sign code:
 * A → DANGER, B → PRIORITY, C → PROHIBITION, D → MANDATORY,
 * E → PARKING, F → INFORMATION, G → ADDITIONAL,
 * M → CYCLIST, TYPE-* → DELINEATION, Z → ZONE
 */
public enum SignCategory {
    DANGER,
    PRIORITY,
    PROHIBITION,
    MANDATORY,
    PARKING,
    INFORMATION,
    ADDITIONAL,
    CYCLIST,
    DELINEATION,
    ZONE
}
