package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.EventEntityType;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponse(
        UUID id,
        EventEntityType entityType,
        UUID entityId,
        String entityName,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        UUID createdBy,
        String creatorName,
        LocalDateTime createdAt
) {}
