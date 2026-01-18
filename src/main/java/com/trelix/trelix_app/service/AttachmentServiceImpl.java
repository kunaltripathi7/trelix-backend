package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.response.AttachmentResponse;
import com.trelix.trelix_app.entity.Attachment;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.EntityType;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.exception.BadRequestException;

import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.AttachmentRepository;
import com.trelix.trelix_app.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif",
            "application/pdf",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/zip", "application/x-zip-compressed", "application/octet-stream");

    @Override
    @Transactional
    public AttachmentResponse uploadAttachment(MultipartFile file, EntityType entityType, UUID entityId,
            UUID uploaderId) {
        validateFile(file);
        authorizationService.verifyEntityAccess(entityType, entityId, uploaderId);

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
        return AttachmentResponse.from(attachment, uploader);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByEntity(EntityType entityType, UUID entityId, UUID requesterId) {
        authorizationService.verifyEntityAccess(entityType, entityId, requesterId);

        List<Attachment> attachments = attachmentRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType,
                entityId);
        return attachments.stream()
                .map(attachment -> {
                    User uploader = userRepository.findById(attachment.getUploadedBy()).orElse(null);
                    return AttachmentResponse.from(attachment, uploader);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getAttachmentById(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        authorizationService.verifyEntityAccess(attachment.getEntityType(), attachment.getEntityId(), requesterId);

        User uploader = userRepository.findById(attachment.getUploadedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Uploader not found for attachment"));

        return AttachmentResponse.from(attachment, uploader);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDownloadUrl(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        authorizationService.verifyEntityAccess(attachment.getEntityType(), attachment.getEntityId(), requesterId);

        try {
            String fullPublicId = cloudinaryService.extractPublicId(attachment.getUrl());
            String resourceType = determineResourceType(attachment.getFileType());
            String generatedUrl = cloudinaryService.generateDownloadUrl(fullPublicId, resourceType);
            log.info("Generating Cloudinary URL. PublicID: {}, Type: {}, URL: {}", fullPublicId, resourceType,
                    generatedUrl);
            return generatedUrl;
        } catch (Exception e) {
            log.warn("Failed to generate Cloudinary download URL, falling back to stored URL: {}", e.getMessage());
            return attachment.getUrl();
        }
    }

    private String determineResourceType(String mimeType) {
        // cloudinary treats pdf as image
        if (mimeType.startsWith("image/") || mimeType.equals("application/pdf")) {
            return "image";
        }
        return "raw";
    }

    @Override
    @Transactional
    public void deleteAttachment(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        authorizationService.verifyAttachmentDeletion(attachment, requesterId);

        try {
            String publicId = cloudinaryService.extractPublicId(attachment.getUrl());
            cloudinaryService.deleteFile(publicId);
        } catch (Exception e) {
            log.error("Failed to delete file from Cloudinary: {}", e.getMessage());
        }

        attachmentRepository.delete(attachment);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File cannot be empty", ErrorCode.INVALID_INPUT);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB limit",
                    ErrorCode.FILE_UPLOAD_MAX_SIZE_EXCEEDED);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BadRequestException("File type not allowed: " + contentType, ErrorCode.INVALID_INPUT);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..") || originalFilename.contains("/")
                || originalFilename.contains("\\")) {
            throw new BadRequestException("Invalid filename: " + originalFilename, ErrorCode.INVALID_INPUT);
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }

}
