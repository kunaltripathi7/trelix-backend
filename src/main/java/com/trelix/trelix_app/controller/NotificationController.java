package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.NotificationDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.NotificationService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

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
