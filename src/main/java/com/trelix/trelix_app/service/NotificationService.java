package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.CreateNotificationRequest;
import com.trelix.trelix_app.dto.NotificationResponse;
import com.trelix.trelix_app.dto.PagedNotificationResponse;
import com.trelix.trelix_app.enums.NotificationType;

import java.util.UUID;

public interface NotificationService {
    NotificationResponse createNotification(CreateNotificationRequest request);

    PagedNotificationResponse getNotifications(UUID userId, Boolean isRead, NotificationType type, int page, int size);

    NotificationResponse getNotificationById(UUID notificationId, UUID requesterId);

    long getUnreadCount(UUID userId);

    void markAsRead(UUID notificationId, UUID userId);

    void markAsUnread(UUID notificationId, UUID userId);

    void markAllAsRead(UUID userId);

    void deleteNotification(UUID notificationId, UUID userId);
}
