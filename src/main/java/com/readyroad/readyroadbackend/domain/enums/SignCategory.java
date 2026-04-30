package com.readyroad.readyroadbackend.domain.enums;

/**
 * Category for road signs in the Sign Quiz System.
 * Maps to the leading code family of the Belgian sign identifier:
 * danger, priority, prohibition, mandatory, parking, information,
 * additional, cyclist, delineation, and zone.
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
    ZONE,
    /** Road-management signs such as F39, F79, and F81. */
    ROAD_MANAGEMENT
}
