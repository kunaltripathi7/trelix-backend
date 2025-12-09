package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.AttachmentResponse;
import com.trelix.trelix_app.enums.EntityType;
import com.trelix.trelix_app.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/attachments")
@RequiredArgsConstructor
@Validated
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityType") EntityType entityType,
            @RequestParam("entityId") UUID entityId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID uploaderId = UUID.fromString(userDetails.getUsername());
        AttachmentResponse response = attachmentService.uploadAttachment(file, entityType, entityId, uploaderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
            @RequestParam EntityType entityType,
            @RequestParam UUID entityId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID requesterId = UUID.fromString(userDetails.getUsername());
        List<AttachmentResponse> responses = attachmentService.getAttachmentsByEntity(entityType, entityId, requesterId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{attachmentId}")
    public ResponseEntity<AttachmentResponse> getAttachmentById(
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID requesterId = UUID.fromString(userDetails.getUsername());
        AttachmentResponse response = attachmentService.getAttachmentById(attachmentId, requesterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<Void> downloadAttachment(
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID requesterId = UUID.fromString(userDetails.getUsername());
        String downloadUrl = attachmentService.getDownloadUrl(attachmentId, requesterId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(downloadUrl))
                .build();
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID requesterId = UUID.fromString(userDetails.getUsername());
        attachmentService.deleteAttachment(attachmentId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
