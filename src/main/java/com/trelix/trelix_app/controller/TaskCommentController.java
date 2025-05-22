package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.TaskCommentDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.TaskCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class TaskCommentController {

    @Autowired
    private AuthorizationService authService;

    @Autowired
    private TaskCommentService taskCommentService;

    @PostMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/comments")
    public ResponseEntity<TaskCommentDTO> createComment(@PathVariable UUID teamId,
                                                               @PathVariable UUID projectId,
                                                               @PathVariable UUID taskId,
                                                               @RequestBody TaskCommentDTO commentDTO,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        TaskCommentDTO newTaskCommentCreated = taskCommentService.createComment(taskId, commentDTO, userDetails.getId());
        return ResponseEntity.ok(newTaskCommentCreated);
    }

    @GetMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/comments")
    public ResponseEntity<List<TaskCommentDTO>> getComments(@PathVariable UUID teamId,
                                                            @PathVariable UUID projectId,
                                                            @PathVariable UUID taskId,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        List<TaskCommentDTO> comments = taskCommentService.getComments(taskId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/comments/{commentId}")
    public ResponseEntity<TaskCommentDTO> getComment(@PathVariable UUID teamId,
                                                     @PathVariable UUID projectId,
                                                     @PathVariable UUID taskId,
                                                     @PathVariable UUID commentId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        TaskCommentDTO comment = taskCommentService.getComment(taskId, commentId);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/comments/{commentId}")
    public ResponseEntity<TaskCommentDTO> updateComment(@PathVariable UUID teamId,
                                                        @PathVariable UUID projectId,
                                                        @PathVariable UUID taskId,
                                                        @PathVariable UUID commentId,
                                                        @RequestBody TaskCommentDTO commentDTO,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        TaskCommentDTO updatedComment = taskCommentService.updateComment(taskId, commentId, commentDTO, userDetails.getId());
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID teamId,
                                                @PathVariable UUID projectId,
                                                @PathVariable UUID taskId,
                                                @PathVariable UUID commentId,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        taskCommentService.deleteComment(taskId, commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }


}
