package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Notification;
import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class LearningNotificationWorker {
    private final JdbcTemplate jdbc;
    private final NotificationRepository notifications;
    private final LearningNotificationTransport transport;
    private final TransactionTemplate tx;
    private final java.util.concurrent.ExecutorService deliveryExecutor = java.util.concurrent.Executors.newSingleThreadExecutor(
            Thread.ofPlatform().daemon(true).name("learning-notification-delivery").factory());
    private final java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean();
    @Value("${readyroad.notifications.outbox-enabled:false}")
    private boolean enabled;

    public LearningNotificationWorker(JdbcTemplate jdbc, NotificationRepository notifications,
            LearningNotificationTransport transport, PlatformTransactionManager transactions) {
        this.jdbc = jdbc;
        this.notifications = notifications;
        this.transport = transport;
        this.tx = new TransactionTemplate(transactions);
        this.tx.setPropagationBehavior(org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Scheduled(fixedDelayString = "${readyroad.notifications.poll-ms:5000}")
    public void run() {
        if (!enabled || deliveryExecutor.isShutdown() || !running.compareAndSet(false, true)) return;
        try {
            deliveryExecutor.execute(() -> {
                try { processQueue();
                } catch (Exception failure) {
                    log.warn("Learning notification queue unavailable: errorType={}", failure.getClass().getSimpleName());
                } finally { running.set(false); }
            });
        } catch (java.util.concurrent.RejectedExecutionException stopped) { running.set(false); }
    }

    @jakarta.annotation.PreDestroy
    public void stop() {
        deliveryExecutor.shutdownNow();
    }

    private void processQueue() {
        List<UUID> events = jdbc.query("""
                SELECT id FROM learning_notification_outbox WHERE notification_id IS NULL
                AND next_attempt_at <= CURRENT_TIMESTAMP ORDER BY next_attempt_at, id LIMIT 20
                """, (rs, row) -> rs.getObject(1, UUID.class));
        for (UUID id : events) materialize(id);
        List<Long> deliveries = jdbc.query("""
                SELECT id FROM learning_notification_deliveries WHERE status = 'PENDING'
                AND next_attempt_at <= CURRENT_TIMESTAMP ORDER BY next_attempt_at, id LIMIT 20
                """, (rs, row) -> rs.getLong(1));
        for (Long id : deliveries) deliver(id);
    }

    void materialize(UUID id) {
        try {
            tx.executeWithoutResult(status -> {
                var rows = jdbc.query("""
                        SELECT * FROM learning_notification_outbox WHERE id = ? AND notification_id IS NULL
                        AND next_attempt_at <= CURRENT_TIMESTAMP FOR UPDATE SKIP LOCKED
                        """, LearningNotificationWorker::notification, id);
                if (rows.isEmpty()) return;
                Notification notification = notifications.saveAndFlush(rows.getFirst());
                Long userId = notification.getUserId();
                // Channel preference is checked again immediately before sending.
                jdbc.update("""
                        INSERT INTO learning_notification_deliveries(outbox_id, channel, recipient_key)
                        SELECT ?, 'EMAIL', 'email' FROM learning_notification_preferences p
                        JOIN users u ON u.id = p.user_id
                        WHERE p.user_id = ? AND p.email_enabled AND u.email_verified
                        AND u.is_active AND u.role = 'USER'
                        ON CONFLICT DO NOTHING
                        """, id, userId);
                jdbc.update("""
                        INSERT INTO learning_notification_deliveries(outbox_id, channel, recipient_key, subscription_id)
                        SELECT ?, 'PUSH', CAST(s.id AS VARCHAR), s.id FROM learning_push_subscriptions s
                        JOIN users u ON u.id = s.user_id
                        WHERE s.user_id = ? AND s.active AND u.is_active AND u.role = 'USER'
                        ON CONFLICT DO NOTHING
                        """, id, userId);
                jdbc.update("UPDATE learning_notification_outbox SET notification_id = ?, last_error_code = NULL WHERE id = ?",
                        notification.getId(), id);
            });
        } catch (Exception failure) {
            retry("learning_notification_outbox", id, failure);
        }
    }

    void deliver(Long id) {
        try {
            tx.executeWithoutResult(status -> {
                var rows = jdbc.query("""
                        SELECT d.channel, d.subscription_id, o.* FROM learning_notification_deliveries d
                        JOIN learning_notification_outbox o ON o.id = d.outbox_id
                        WHERE d.id = ? AND d.status = 'PENDING' AND d.next_attempt_at <= CURRENT_TIMESTAMP
                        FOR UPDATE OF d SKIP LOCKED
                        """, (rs, row) -> new Delivery(rs.getString("channel"),
                        rs.getObject("subscription_id", UUID.class), notification(rs, row)), id);
                if (rows.isEmpty()) return;
                var delivery = rows.getFirst();
                boolean sent = transport.send(delivery.channel(), delivery.subscriptionId(), delivery.notification());
                jdbc.update("""
                        UPDATE learning_notification_deliveries SET status = ?, delivered_at = CURRENT_TIMESTAMP,
                        last_error_code = NULL WHERE id = ?
                        """, sent ? "SENT" : "CANCELLED", id);
            });
        } catch (Exception failure) {
            retry("learning_notification_deliveries", id, failure);
        }
    }

    private void retry(String table, Object id, Exception failure) {
        // Only these two internal table names are accepted; exception messages may contain provider secrets.
        String pending = table.equals("learning_notification_outbox") ? "notification_id IS NULL" : "status = 'PENDING'";
        tx.executeWithoutResult(status -> {
            var existing = jdbc.query("SELECT attempts FROM " + table + " WHERE id = ? FOR UPDATE",
                    (rs, row) -> rs.getInt(1), id);
            if (existing.isEmpty()) return;
            int attempts = existing.getFirst();
            jdbc.update("UPDATE " + table + " SET attempts = attempts + 1, last_error_code = ?, "
                    + "next_attempt_at = CURRENT_TIMESTAMP + (? * INTERVAL '1 second') WHERE id = ? AND " + pending,
                    failure instanceof LearningNotificationTransport.PushDeliveryException push
                            ? "PUSH_HTTP_" + push.status : failure.getClass().getSimpleName(),
                    Math.max(LearningNotificationOutbox.retrySeconds(attempts + 1),
                            failure instanceof LearningNotificationTransport.PushDeliveryException push
                                    ? push.retryAfterSeconds : 0), id);
        });
        log.warn("Learning notification will retry: queue={}, id={}, errorType={}",
                table, id, failure.getClass().getSimpleName());
    }

    static Notification notification(ResultSet rs, int row) throws SQLException {
        return Notification.builder().id(rs.getObject("notification_id", Long.class)).userId(rs.getLong("user_id"))
                .type(NotificationType.valueOf(rs.getString("type")))
                .title(rs.getString("title")).message(rs.getString("message"))
                .messageKey(rs.getString("message_key")).messageParams(rs.getString("message_params"))
                .link(rs.getString("link")).createdAt(rs.getTimestamp("created_at").toInstant()).build();
    }

    private record Delivery(String channel, UUID subscriptionId, Notification notification) {}
}
