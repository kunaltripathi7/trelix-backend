package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.AttachmentResponse;
import com.trelix.trelix_app.entity.Attachment;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.EntityType;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.exception.ServiceException;
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
    private final TaskService taskService; // For task entity access verification
    private final MessageService messageService; // For message entity access verification
    // Assuming existence of auth services for delete permissions
    // private final TaskAuthService taskAuthService;
    // private final MessageAuthService messageAuthService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif",
            "application/pdf",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc, .docx
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls, .xlsx
            "application/zip", "application/x-zip-compressed"
    );

    @Override
    @Transactional
    public AttachmentResponse uploadAttachment(MultipartFile file, EntityType entityType, UUID entityId, UUID uploaderId) {
        validateFile(file);
        verifyEntityAccess(entityType, entityId, uploaderId); // Verify uploader has access to parent entity

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
        verifyEntityAccess(entityType, entityId, requesterId); // Verify requester has access to parent entity

        List<Attachment> attachments = attachmentRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
        return attachments.stream()
                .map(attachment -> {
                    User uploader = userRepository.findById(attachment.getUploadedBy()).orElse(null); // Uploader might be deleted
                    return AppMapper.convertToAttachmentResponse(attachment, uploader);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getAttachmentById(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        verifyEntityAccess(attachment.getEntityType(), attachment.getEntityId(), requesterId); // Verify requester has access to parent entity

        User uploader = userRepository.findById(attachment.getUploadedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Uploader not found for attachment"));

        return AppMapper.convertToAttachmentResponse(attachment, uploader);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDownloadUrl(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        verifyEntityAccess(attachment.getEntityType(), attachment.getEntityId(), requesterId); // Verify requester has access to parent entity

        // Cloudinary URLs are directly downloadable, no special "download flag" needed for public URLs
        // If private downloads were required, Cloudinary SDK has methods for signed URLs.
        return attachment.getUrl();
    }

    @Override
    @Transactional
    public void deleteAttachment(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        verifyDeletePermission(attachment, requesterId); // Verify requester has permission to delete

        // Delete from Cloudinary first
        try {
            String publicId = cloudinaryService.extractPublicId(attachment.getUrl());
            cloudinaryService.deleteFile(publicId);
        } catch (Exception e) {
            // Log the error but proceed with DB deletion to avoid orphaned DB records
            System.err.println("Failed to delete file from Cloudinary: " + e.getMessage());
            // Optionally, throw a custom exception or mark for retry
        }

        // Delete from database
        attachmentRepository.delete(attachment);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB limit");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BadRequestException("File type not allowed: " + contentType);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BadRequestException("Invalid filename: " + originalFilename);
        }
    }

    private String sanitizeFilename(String filename) {
        // Remove path traversal attempts and special characters, keep dots for extensions
        return filename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }

    private void verifyEntityAccess(EntityType entityType, UUID entityId, UUID userId) {
        switch (entityType) {
            case TASK -> {
                // This method in TaskService is assumed to throw an exception if the user doesn't have access
                taskService.getTaskById(entityId, userId);
            }
            case MESSAGE -> {
                // This method in MessageService is assumed to throw an exception if the user doesn't have access
                messageService.getMessageById(entityId, userId);
            }
            default -> throw new BadRequestException("Unsupported entity type for attachment: " + entityType);
        }
    }

    private void verifyDeletePermission(Attachment attachment, UUID requesterId) {
        // Check if user is uploader
        if (attachment.getUploadedBy().equals(requesterId)) {
            return;
        }

        // Assuming existence of dedicated authorization services or methods within existing services
        // For now, I'll add placeholder comments for where these checks would go.
        // In a real application, these would be injected services like TaskAuthService, MessageAuthService.

        // Example for TASK: Check if requester is an admin of the project/team associated with the task
        if (attachment.getEntityType() == EntityType.TASK) {
            // taskAuthService.verifyTaskAdminAccess(attachment.getEntityId(), requesterId);
            // For now, if not uploader, and it's a task, we'll throw forbidden.
            // This needs to be replaced with actual admin check.
            throw new ForbiddenException("You do not have permission to delete this task attachment.");
        }
        // Example for MESSAGE: Check if requester is an admin of the channel/DM associated with the message
        else if (attachment.getEntityType() == EntityType.MESSAGE) {
            // messageAuthService.verifyMessageDeleteAccess(attachment.getEntityId(), requesterId);
            // For now, if not uploader, and it's a message, we'll throw forbidden.
            // This needs to be replaced with actual admin check.
            throw new ForbiddenException("You do not have permission to delete this message attachment.");
        } else {
            throw new ForbiddenException("You do not have permission to delete this attachment.");
        }
    }
}
