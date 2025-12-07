package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.CreateNotificationRequest;
import com.trelix.trelix_app.dto.NotificationResponse;
import com.trelix.trelix_app.dto.PagedNotificationResponse;
import com.trelix.trelix_app.dto.UnreadCountResponse;
import com.trelix.trelix_app.enums.NotificationType;
import com.trelix.trelix_app.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    // TEMPORARY - Remove when Kafka implemented
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody CreateNotificationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // For testing purposes, we can allow any authenticated user to create a notification
        // In a real scenario, this would be handled by internal services or Kafka consumers.
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(request));
    }

    @GetMapping
    public ResponseEntity<PagedNotificationResponse> getNotifications(
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        PagedNotificationResponse response = notificationService.getNotifications(userId, isRead, type, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        NotificationResponse response = notificationService.getNotificationById(notificationId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PatchMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{notificationId}/unread")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> markAsUnread(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        notificationService.markAsUnread(notificationId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/mark-all-read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        notificationService.deleteNotification(notificationId, userId);
        return ResponseEntity.noContent().build();
    }
}
