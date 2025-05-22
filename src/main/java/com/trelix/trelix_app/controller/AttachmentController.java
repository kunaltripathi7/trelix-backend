package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.service.AttachmentService;
import com.trelix.trelix_app.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AttachmentController {

//    POST   /teams/{teamId}/projects/{projectId}/tasks/{taskId}/attachments — upload
//    GET    /teams/{teamId}/projects/{projectId}/tasks/{taskId}/attachments — list all
//    DELETE /teams/{teamId}/projects/{projectId}/tasks/{taskId}/attachments/{attachmentId} — delete


    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AuthorizationService authService;

    @PostMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/attachments")
    public ResponseEntity<String> uploadAttachment(@PathVariable UUID teamId,
                                                   @PathVariable UUID projectId,
                                                   @PathVariable UUID taskId,

    {
        authService.checkTaskAccess();
        // Call the service to handle the upload logic
        attachmentService.uploadAttachment(teamId, projectId, taskId);
        return ResponseEntity.ok("Attachment uploaded successfully");

    }
}
