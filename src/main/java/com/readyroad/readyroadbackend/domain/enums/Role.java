package com.readyroad.readyroadbackend.domain.enums;

/**
 * User Role Enumeration
 *
 * Defines the roles available in the ReadyRoad system:
 * - USER: Regular user with access to quiz features
 * - ADMIN: Administrative user with full access
 * - MODERATOR: User with moderation capabilities
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 */
public enum Role {
    /**
     * Regular user role
     * - Can take quizzes
     * - Can view own statistics
     * - Cannot access admin features
     */
    USER,

    /**
     * Moderator role
     * - Can review user submissions
     * - Can manage content
     * - Limited admin capabilities
     */
    MODERATOR,

    /**
     * Administrator role
     * - Full system access
     * - Can manage users
     * - Can configure system settings
     */
    ADMIN;

    /**
     * Get default role for new users
     *
     * @return Default role (USER)
     */
    public static Role getDefault() {
        return USER;
    }
}
