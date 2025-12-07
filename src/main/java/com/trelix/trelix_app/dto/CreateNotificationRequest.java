package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.NotificationType;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record CreateNotificationRequest(
        @NotNull UUID notifierId,
        @NotNull UUID actorId,
        @NotNull NotificationType type,
        @NotNull UUID referenceId,
        Map<String, String> metadata
) {}
