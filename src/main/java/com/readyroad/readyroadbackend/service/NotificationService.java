package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;

    public int getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // For now, return 0 as we don't have a notifications table yet
        // This can be extended later with a proper Notification entity
        return 0;
    }
}
