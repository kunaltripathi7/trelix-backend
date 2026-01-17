package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.request.CreateNotificationRequest;
import com.trelix.trelix_app.dto.response.NotificationResponse;
import com.trelix.trelix_app.dto.response.PagedNotificationResponse;
import com.trelix.trelix_app.dto.response.UnreadCountResponse;
import com.trelix.trelix_app.enums.NotificationType;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
@Validated
@Tag(name = "Notifications", description = "User notification system with read/unread status")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create notification", description = "Create a notification for a user (internal use)")
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody CreateNotificationRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(request));
    }

    @GetMapping
    @Operation(summary = "Get notifications", description = "Get paginated notifications with optional filters for read status and type")
    public ResponseEntity<PagedNotificationResponse> getNotifications(
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PagedNotificationResponse response = notificationService.getNotifications(currentUser.getId(), isRead, type,
                page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID", description = "Get a specific notification")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        NotificationResponse response = notificationService.getNotificationById(notificationId, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PatchMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Mark as read", description = "Mark a notification as read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        notificationService.markAsRead(notificationId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{notificationId}/unread")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Mark as unread", description = "Mark a notification as unread")
    public ResponseEntity<Void> markAsUnread(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        notificationService.markAsUnread(notificationId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/mark-all-read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete notification", description = "Delete a notification")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        notificationService.deleteNotification(notificationId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
