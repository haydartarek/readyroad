package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository
 *
 * Enhanced with authentication support (username-based login)
 *
 * @author ReadyRoad Team
 * @since 2026-01-18 (Enhanced with JWT support)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Email-based methods
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    // Username-based methods (for authentication)
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByUsernameAndIsActiveTrue(String username);

    // ========== RBAC Support Methods (Added 2026-02-04) ==========

    /**
     * Find all active users (for scheduler operations like study reminders).
     */
    List<User> findByIsActiveTrue();

    /**
     * Count active users
     * Used by: AdminController.getDashboard()
     */
    long countByIsActiveTrue();

    /**
     * Count users by role
     * Used by: AdminController.getDashboard()
     * 
     * @param role - Role enum (USER, MODERATOR, ADMIN)
     */
    long countByRole(com.readyroad.readyroadbackend.domain.enums.Role role);

    /**
     * Find all users with a given role.
     * Used by: NotificationService.notifyAllAdmins()
     */
    List<User> findByRole(com.readyroad.readyroadbackend.domain.enums.Role role);
}
