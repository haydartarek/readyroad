package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.NotificationDTO;
import com.readyroad.readyroadbackend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Notification Controller
 *
 * All endpoints are scoped to the authenticated user (/api/users/me/...).
 *
 * Endpoints:
 *   GET    /api/users/me/notifications                → list (max 50)
 *   GET    /api/users/me/notifications/unread-count   → {unreadCount: N}
 *   PATCH  /api/users/me/notifications/{id}/read      → mark one as read
 *   PATCH  /api/users/me/notifications/read-all       → mark all as read
 */
@Slf4j
@RestController
@RequestMapping("/api/users/me/notifications")
@Tag(name = "Notifications", description = "User notification management")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ── GET /api/users/me/notifications ──────────────────────────────────────

    @GetMapping
    @Operation(
        summary     = "Get all notifications",
        description = "Returns the 50 most recent notifications for the authenticated user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthenticated")
    })
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal UserDetails principal) {

        String username = principal.getUsername();
        log.debug("GET notifications for user: {}", username);

        List<NotificationDTO> notifications = notificationService.getAllNotifications(username);
        return ResponseEntity.ok(notifications);
    }

    // ── GET /api/users/me/notifications/unread-count ─────────────────────────

    @GetMapping("/unread-count")
    @Operation(
        summary     = "Get unread notification count",
        description = "Lightweight endpoint polled every 30 s by the navbar"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Count returned"),
        @ApiResponse(responseCode = "401", description = "Unauthenticated")
    })
    public ResponseEntity<Map<String, Integer>> getUnreadCount(
            @AuthenticationPrincipal UserDetails principal) {

        String username = principal.getUsername();
        int count = notificationService.getUnreadCount(username);

        // Key MUST be "unreadCount" — matches frontend userService.ts
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    // ── PATCH /api/users/me/notifications/{id}/read ───────────────────────────

    @PatchMapping("/{id}/read")
    @Operation(
        summary     = "Mark a notification as read",
        description = "Marks a specific notification as read. Idempotent if already read."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Marked as read"),
        @ApiResponse(responseCode = "401", description = "Unauthenticated"),
        @ApiResponse(responseCode = "403", description = "Notification belongs to another user"),
        @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        String username = principal.getUsername();
        log.debug("Marking notification {} as read for user: {}", id, username);

        notificationService.markAsRead(id, username);
        return ResponseEntity.ok().build();
    }

    // ── PATCH /api/users/me/notifications/read-all ────────────────────────────

    @PatchMapping("/read-all")
    @Operation(
        summary     = "Mark all notifications as read",
        description = "Marks every unread notification as read. Called when user opens the notification panel."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All notifications marked as read"),
        @ApiResponse(responseCode = "401", description = "Unauthenticated")
    })
    public ResponseEntity<Map<String, Integer>> markAllAsRead(
            @AuthenticationPrincipal UserDetails principal) {

        String username = principal.getUsername();
        log.info("Marking all notifications as read for user: {}", username);

        int updated = notificationService.markAllAsRead(username);
        return ResponseEntity.ok(Map.of("updated", updated));
    }
}
