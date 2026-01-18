package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.response.PagedNotificationResponse;
import com.trelix.trelix_app.enums.NotificationType;

import java.util.UUID;

public interface NotificationService {

    PagedNotificationResponse getNotifications(UUID userId, Boolean isRead, NotificationType type, int page, int size);

    long getUnreadCount(UUID userId);

    void markAsRead(UUID notificationId, UUID userId);

    void markAllAsRead(UUID userId);

    void deleteNotification(UUID notificationId, UUID userId);
}
