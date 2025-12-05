package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.AttachmentDTO;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.exception.InvalidRequestException;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/attachments")
    public ResponseEntity<AttachmentDTO> uploadAttachment(
            @RequestParam(required = false) UUID taskId,
            @RequestParam(value = "messageId", required = false) UUID messageId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {

        if (messageId == null && taskId == null) {
            throw new InvalidRequestException(
                    "Either taskId or messageId must be provided.",
                    ErrorCode.INVALID_INPUT
            );
        }
        if (messageId != null && taskId != null) {
            throw new InvalidRequestException(
                    "Provide either taskId or messageId, but not both.",
                    ErrorCode.INVALID_INPUT
            );
        }

        AttachmentDTO attachment = attachmentService.uploadAttachment(file, taskId, userDetails.getId(), messageId);
        return ResponseEntity.ok(attachment);
    }

    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentDTO>> getAttachments(@PathVariable UUID taskId,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AttachmentDTO> attachments = attachmentService.getAttachments(taskId, userDetails.getId());
        return ResponseEntity.ok(attachments);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable UUID attachmentId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        attachmentService.deleteAttachment(attachmentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
