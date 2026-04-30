package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    // Username-based methods (for authentication)
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<User> findByUsernameAndIsActiveTrue(String username);

    @Query("""
            SELECT u
            FROM User u
            WHERE LOWER(u.username) = LOWER(:identifier)
               OR LOWER(u.email) = LOWER(:identifier)
            """)
    Optional<User> findByUsernameOrEmailIgnoreCase(@Param("identifier") String identifier);

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

    @Query("""
            SELECT u
            FROM User u
            WHERE (:query IS NULL OR :query = ''
                OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
                OR STR(u.id) LIKE CONCAT('%', :query, '%'))
            """)
    Page<User> findAdminUsers(@Param("query") String query, Pageable pageable);
}
