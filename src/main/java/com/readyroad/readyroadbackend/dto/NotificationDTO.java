package com.readyroad.readyroadbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.readyroad.readyroadbackend.domain.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Notification DTO — sent to the frontend.
 *
 * Fields match what the frontend Notification interface expects:
 *   id, type, title, message, link, isRead, createdAt, readAt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long    id;
    private String  type;        // NotificationType name (EXAM_PASSED, EXAM_FAILED, …)
    private String  title;
    private String  message;
    private String  link;        // Optional deep-link, e.g. "/exam/results/42"
    private String  messageKey;  // i18n key for translated message rendering (nullable)
    private String  messageParams; // JSON params for messageKey interpolation (nullable)
    @JsonProperty("isRead")   // Force serialization as "isRead" (not "read")
    private boolean isRead;
    private Instant createdAt;
    private Instant readAt;      // null while unread

    /** Factory: convert entity → DTO */
    public static NotificationDTO from(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .type(n.getType().name())
                .title(n.getTitle())
                .message(n.getMessage())
                .link(n.getLink())
                .messageKey(n.getMessageKey())
                .messageParams(n.getMessageParams())
                .isRead(Boolean.TRUE.equals(n.getIsRead()))
                .createdAt(n.getCreatedAt())
                .readAt(n.getReadAt())
                .build();
    }
}
