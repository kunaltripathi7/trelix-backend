package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.EntityType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttachmentResponse(
        UUID id,
        String fileName,
        String fileType,
        Long fileSize,
        String url,
        UUID uploadedBy,
        String uploaderName,
        EntityType entityType,
        UUID entityId,
        LocalDateTime createdAt
) {}
