package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Notification;
import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LearningNotificationOutbox {
    private final JdbcTemplate jdbc;

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(Notification notification) {
        // Serialize competing reminders for the same learner, across workers/nodes.
        jdbc.queryForObject("SELECT id FROM users WHERE id = ? FOR UPDATE", Long.class, notification.getUserId());
        if ((notification.getType() == NotificationType.STUDY_REMINDER
                || notification.getType() == NotificationType.WEAK_AREA)
                && hasRecent(notification.getUserId(), notification.getType(), Instant.now().minusSeconds(86400))) {
            return;
        }
        jdbc.update("""
                INSERT INTO learning_notification_outbox
                (id, user_id, type, title, message, message_key, message_params, link, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, UUID.randomUUID(), notification.getUserId(), notification.getType().name(),
                notification.getTitle(), notification.getMessage(), notification.getMessageKey(),
                notification.getMessageParams(), notification.getLink(), Timestamp.from(notification.getCreatedAt()));
    }

    public boolean hasRecent(Long userId, NotificationType type, Instant cutoff) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS (SELECT 1 FROM learning_notification_outbox
                WHERE user_id = ? AND type = ? AND created_at > ?)
                """, Boolean.class, userId, type.name(), Timestamp.from(cutoff)));
    }

    static long retrySeconds(int attempts) {
        return Math.min(3600, 30L << Math.min(7, Math.max(0, attempts - 1)));
    }
}
