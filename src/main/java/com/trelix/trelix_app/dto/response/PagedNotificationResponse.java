package com.trelix.trelix_app.dto.response;

import java.util.List;

public record PagedNotificationResponse(
        List<NotificationResponse> notifications,
        int currentPage,
        int totalPages,
        long totalElements,
        long unreadCount
) {}




