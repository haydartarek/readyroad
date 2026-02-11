package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    /**
     * Get all notifications for a user
     * For now, returns empty list as we don't have a notifications table yet
     */
    public List<NotificationDTO> getAllNotifications(String username) {
        log.info("Getting all notifications for user: {}", username);
        // TODO: Implement database query when notifications table is ready
        return new ArrayList<>();
    }

    /**
     * Get unread notification count for a user
     * For now, returns 0 as we don't have a notifications table yet
     */
    public int getUnreadCount(String username) {
        log.info("Getting unread notification count for user: {}", username);
        // TODO: Implement database query when notifications table is ready
        return 0;
    }

    /**
     * Mark a notification as read
     */
    public void markAsRead(Long notificationId, String username) {
        log.info("Marking notification {} as read for user: {}", notificationId, username);
        // TODO: Implement database update when notifications table is ready
    }
}
