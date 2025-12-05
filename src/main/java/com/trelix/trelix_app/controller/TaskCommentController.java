package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.TaskCommentDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.TaskCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<TaskCommentDTO> createComment(@PathVariable UUID taskId,
                                                        @RequestBody TaskCommentDTO commentDTO,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskCommentDTO newComment = taskCommentService.createComment(taskId, commentDTO, userDetails.getId());
        return ResponseEntity.ok(newComment);
    }

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<TaskCommentDTO>> getComments(@PathVariable UUID taskId,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TaskCommentDTO> comments = taskCommentService.getComments(taskId, userDetails.getId());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<TaskCommentDTO> getComment(@PathVariable UUID commentId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskCommentDTO comment = taskCommentService.getComment(commentId, userDetails.getId());
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<TaskCommentDTO> updateComment(@PathVariable UUID commentId,
                                                        @RequestBody TaskCommentDTO commentDTO,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskCommentDTO updatedComment = taskCommentService.updateComment(commentId, commentDTO, userDetails.getId());
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        taskCommentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
