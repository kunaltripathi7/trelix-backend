package com.trelix.trelix_app.dto.response;

import com.trelix.trelix_app.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID notifierId,
        UUID actorId,
        String actorName,
        NotificationType type,
        UUID referenceId,
        String message,
        Map<String, String> metadata,
        boolean isRead,
        LocalDateTime createdAt
) {}




