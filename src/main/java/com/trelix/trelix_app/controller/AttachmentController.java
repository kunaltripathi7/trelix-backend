package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.AttachmentDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AttachmentService;
import com.trelix.trelix_app.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AuthorizationService authService;

    @PostMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/attachments")
    public ResponseEntity<String> uploadAttachment(@PathVariable UUID teamId,
                                                   @PathVariable UUID projectId,
                                                   @PathVariable UUID taskId,
                                                   @RequestParam("file") MultipartFile file,
                                                   @RequestParam(value = "messageId", required = false) UUID messageId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        authService.checkTaskAccess(teamId, projectId, taskId, userDetails.getId());
        attachmentService.uploadAttachment(file, taskId, userDetails.getId(), messageId);
        return ResponseEntity.ok("Attachment uploaded successfully");

    }

    @GetMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentDTO>> getAttachments(@PathVariable UUID teamId,
                                                                 @PathVariable UUID projectId,
                                                                 @PathVariable UUID taskId,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkTaskAccess(teamId, projectId, taskId, userDetails.getId());
        List<AttachmentDTO> attachments = attachmentService.getAttachments(taskId);
        return ResponseEntity.ok(attachments);
    }

    @DeleteMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/attachments/{attachmentId}")
    public ResponseEntity<String> deleteAttachment(@PathVariable UUID teamId,
                                                   @PathVariable UUID projectId,
                                                   @PathVariable UUID taskId,
                                                   @PathVariable UUID attachmentId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkTaskAccess(teamId, projectId, taskId, userDetails.getId());
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.ok("Attachment deleted successfully");
    }
}
