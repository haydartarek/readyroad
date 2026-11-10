package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.service.UserFavoriteRoadSignService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users/me/favorites")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class UserFavoriteRoadSignController {
    private final UserFavoriteRoadSignService favorites;

    @GetMapping
    public List<Long> list(@AuthenticationPrincipal User user,
                           @RequestHeader(value = "X-Expected-User-Id", required = false) Long expectedUserId) {
        return favorites.list(owner(user, expectedUserId));
    }

    @PutMapping("/{signId}")
    public ResponseEntity<Void> add(@AuthenticationPrincipal User user,
                                     @RequestHeader(value = "X-Expected-User-Id", required = false) Long expectedUserId,
                                     @PathVariable Long signId) {
        Long userId = owner(user, expectedUserId);
        requirePositiveId(signId);
        favorites.add(userId, signId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{signId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal User user,
                                        @RequestHeader(value = "X-Expected-User-Id", required = false) Long expectedUserId,
                                        @PathVariable Long signId) {
        Long userId = owner(user, expectedUserId);
        requirePositiveId(signId);
        favorites.remove(userId, signId);
        return ResponseEntity.noContent().build();
    }

    private static Long owner(User user, Long expectedUserId) {
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        if (expectedUserId != null && !expectedUserId.equals(user.getId())) {
            // A request started before switching accounts must never mutate the new account.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account changed; retry");
        }
        return user.getId();
    }

    private static void requirePositiveId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sign ID");
        }
    }
}
