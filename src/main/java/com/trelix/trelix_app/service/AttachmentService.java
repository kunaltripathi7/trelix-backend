package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.AttachmentResponse;
import com.trelix.trelix_app.enums.EntityType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {

    AttachmentResponse uploadAttachment(MultipartFile file, EntityType entityType, UUID entityId, UUID uploaderId);

    List<AttachmentResponse> getAttachmentsByEntity(EntityType entityType, UUID entityId, UUID requesterId);

    AttachmentResponse getAttachmentById(UUID attachmentId, UUID requesterId);

    String getDownloadUrl(UUID attachmentId, UUID requesterId);

    void deleteAttachment(UUID attachmentId, UUID requesterId);
}
