package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.AttachmentResponse;
import com.trelix.trelix_app.entity.Attachment;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.EntityType;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.AttachmentRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final TaskService taskService;
    private final MessageService messageService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif",
            "application/pdf",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/zip", "application/x-zip-compressed"
    );

    @Override
    @Transactional
    public AttachmentResponse uploadAttachment(MultipartFile file, EntityType entityType, UUID entityId, UUID uploaderId) {
        validateFile(file);
        verifyEntityAccess(entityType, entityId, uploaderId);

        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new ResourceNotFoundException("Uploader not found"));

        String folder = "trelix/" + entityType.name().toLowerCase() + "/" + entityId;
        String sanitizedFileName = sanitizeFilename(file.getOriginalFilename());
        String fileUrl = cloudinaryService.uploadFile(file, folder);

        Attachment attachment = Attachment.builder()
                .fileName(sanitizedFileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .url(fileUrl)
                .uploadedBy(uploaderId)
                .entityType(entityType)
                .entityId(entityId)
                .build();

        attachment = attachmentRepository.save(attachment);
        return AppMapper.convertToAttachmentResponse(attachment, uploader);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByEntity(EntityType entityType, UUID entityId, UUID requesterId) {
        verifyEntityAccess(entityType, entityId, requesterId);

        List<Attachment> attachments = attachmentRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
        return attachments.stream()
                .map(attachment -> {
                    User uploader = userRepository.findById(attachment.getUploadedBy()).orElse(null);
                    return AppMapper.convertToAttachmentResponse(attachment, uploader);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getAttachmentById(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        verifyEntityAccess(attachment.getEntityType(), attachment.getEntityId(), requesterId);

        User uploader = userRepository.findById(attachment.getUploadedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Uploader not found for attachment"));

        return AppMapper.convertToAttachmentResponse(attachment, uploader);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDownloadUrl(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        verifyEntityAccess(attachment.getEntityType(), attachment.getEntityId(), requesterId);
        return attachment.getUrl();
    }

    @Override
    @Transactional
    public void deleteAttachment(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        verifyDeletePermission(attachment, requesterId);

        try {
            String publicId = cloudinaryService.extractPublicId(attachment.getUrl());
            cloudinaryService.deleteFile(publicId);
        } catch (Exception e) {
            System.err.println("Failed to delete file from Cloudinary: " + e.getMessage());
        }

        attachmentRepository.delete(attachment);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File cannot be empty", ErrorCode.INVALID_INPUT);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB limit", ErrorCode.FILE_UPLOAD_MAX_SIZE_EXCEEDED);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BadRequestException("File type not allowed: " + contentType, ErrorCode.INVALID_INPUT);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BadRequestException("Invalid filename: " + originalFilename, ErrorCode.INVALID_INPUT);
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }

    private void verifyEntityAccess(EntityType entityType, UUID entityId, UUID userId) {
        try {
            switch (entityType) {
                case TASK -> taskService.getTaskById(entityId, userId);
                case MESSAGE -> messageService.getMessageById(entityId, userId);
                default -> throw new BadRequestException("Unsupported entity type for attachment: " + entityType, ErrorCode.INVALID_INPUT);
            }
        } catch (ResourceNotFoundException e) {
            throw new ForbiddenException("You do not have access to this entity.", ErrorCode.FORBIDDEN);
        }
    }

    private void verifyDeletePermission(Attachment attachment, UUID requesterId) {
        if (attachment.getUploadedBy().equals(requesterId)) {
            return;
        }
        throw new ForbiddenException("You do not have permission to delete this attachment.", ErrorCode.FORBIDDEN);
    }
}
