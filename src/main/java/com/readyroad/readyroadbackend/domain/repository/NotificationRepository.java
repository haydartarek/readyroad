package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.Notification;
import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Notification Repository
 *
 * All queries are scoped to a specific userId (row-level security).
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * All notifications for a user, newest first.
     * Limit to the most recent 50 to avoid UI overload.
     */
    @Query("""
        SELECT n FROM Notification n
        WHERE n.userId = :userId
        ORDER BY n.createdAt DESC
        LIMIT 50
        """)
    List<Notification> findTop50ByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * Count of unread notifications for a user.
     * Used by the navbar polling mechanism.
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false")
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * Find a single notification by ID and owner (prevents cross-user access).
     */
    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    /**
     * Find recent notifications for a user of a specific type, created after a given instant.
     * Used for deduplication (WEAK_AREA, STUDY_REMINDER cooldown).
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.type = :type AND n.createdAt > :since")
    List<Notification> findByUserIdAndTypeAndCreatedAtAfter(
            @Param("userId") Long userId,
            @Param("type") NotificationType type,
            @Param("since") Instant since);

    /**
     * Mark all unread notifications for a user as read at once.
     * Called when user opens the notification panel.
     */
    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isRead = true, n.readAt = :readAt
        WHERE n.userId = :userId AND n.isRead = false
        """)
    int markAllReadByUserId(@Param("userId") Long userId, @Param("readAt") Instant readAt);
}
