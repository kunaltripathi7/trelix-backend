package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.Attachment;
import com.trelix.trelix_app.entity.User;
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
                LocalDateTime createdAt) {
        public static AttachmentResponse from(Attachment attachment, User uploader) {
                return new AttachmentResponse(
                                attachment.getId(),
                                attachment.getFileName(),
                                attachment.getFileType(),
                                attachment.getFileSize(),
                                attachment.getUrl(),
                                attachment.getUploadedBy(),
                                uploader != null ? uploader.getName() : null,
                                attachment.getEntityType(),
                                attachment.getEntityId(),
                                attachment.getCreatedAt());
        }
}
