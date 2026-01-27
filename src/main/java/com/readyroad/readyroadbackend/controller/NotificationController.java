package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.NotificationCountResponse;
import com.readyroad.readyroadbackend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/notifications")
@Tag(name = "Notifications", description = "User notification management")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count", description = "Returns the count of unread notifications for the authenticated user")
    public ResponseEntity<NotificationCountResponse> getUnreadCount(Authentication authentication) {
        String username = authentication.getName();
        int unreadCount = notificationService.getUnreadCount(username);
        return ResponseEntity.ok(new NotificationCountResponse(unreadCount));
    }
}
