package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.NotificationDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getAllNotifcations(userDetails.getId()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getAllUnreadNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getAllUnreadNotifications(userDetails.getId()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> updateNotificationRead(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.updateNotificationRead(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/notifications/mark-all-read")
    public ResponseEntity<Void> updateAllNotificationsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.updateAllNotificationsRead(userDetails.getId());
        return ResponseEntity.ok().build();
    }


}
