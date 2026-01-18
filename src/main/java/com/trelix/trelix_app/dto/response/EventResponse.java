package com.trelix.trelix_app.dto.response;

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
                LocalDateTime createdAt) {
        public static EventResponse from(com.trelix.trelix_app.entity.Event event, String creatorName,
                        String entityName) {
                return new EventResponse(
                                event.getId(),
                                event.getEntityType(),
                                event.getEntityId(),
                                entityName,
                                event.getTitle(),
                                event.getDescription(),
                                event.getStartTime(),
                                event.getEndTime(),
                                event.getCreatedBy(),
                                creatorName,
                                event.getCreatedAt());
        }
}
