package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.response.AttachmentResponse;
import com.trelix.trelix_app.enums.EntityType;
import com.trelix.trelix_app.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.trelix.trelix_app.security.CustomUserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/attachments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload attachment", description = "Upload a file to an entity")
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityType") EntityType entityType,
            @RequestParam("entityId") UUID entityId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID uploaderId = userDetails.getId();
        AttachmentResponse response = attachmentService.uploadAttachment(file, entityType, entityId, uploaderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get attachments", description = "Get list of attachments for an entity")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
            @RequestParam EntityType entityType,
            @RequestParam UUID entityId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID requesterId = userDetails.getId();
        List<AttachmentResponse> responses = attachmentService.getAttachmentsByEntity(entityType, entityId,
                requesterId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{attachmentId}")
    @Operation(summary = "Get attachment", description = "Get details of an attachment")
    public ResponseEntity<AttachmentResponse> getAttachmentById(
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID requesterId = userDetails.getId();
        AttachmentResponse response = attachmentService.getAttachmentById(attachmentId, requesterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{attachmentId}/download")
    @Operation(summary = "Download attachment", description = "Get redirect URL for downloading the file")
    public ResponseEntity<Void> downloadAttachment(
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID requesterId = userDetails.getId();
        String downloadUrl = attachmentService.getDownloadUrl(attachmentId, requesterId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(downloadUrl))
                .build();
    }

    @DeleteMapping("/{attachmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete attachment", description = "Delete an attachment")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID requesterId = userDetails.getId();
        attachmentService.deleteAttachment(attachmentId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
