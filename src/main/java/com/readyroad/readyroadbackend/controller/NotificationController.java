package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.NotificationDTO;
import com.readyroad.readyroadbackend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/me/notifications")
@Tag(name = "Notifications", description = "User notification management")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get all notifications", description = "Returns all notifications for the authenticated user")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@AuthenticationPrincipal String username) {
        List<NotificationDTO> notifications = notificationService.getAllNotifications(username);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count", description = "Returns the count of unread notifications for the authenticated user")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(@AuthenticationPrincipal String username) {
        int unreadCount = notificationService.getUnreadCount(username);
        return ResponseEntity.ok(Map.of("count", unreadCount));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        notificationService.markAsRead(id, username);
        return ResponseEntity.ok().build();
    }
}
