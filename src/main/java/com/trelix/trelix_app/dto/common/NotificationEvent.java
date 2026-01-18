package com.trelix.trelix_app.dto.common;

import java.util.Map;
import java.util.UUID;

import com.trelix.trelix_app.enums.NotificationType;

public record NotificationEvent(
        UUID recipientId,
        UUID actorId, // who triggered
        NotificationType type,
        String subject,
        String message,
        UUID relatedEntityId, // Task ID or Project ID
        Map<String, String> metadata) {
}
