package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.common.CommentDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentDTO commentDTO,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentDTO createdComment = commentService.createComment(commentDTO, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<List<CommentDTO>> getCommentsForTask(@PathVariable UUID taskId,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CommentDTO> comments = commentService.getCommentsForTask(taskId, userDetails.getId());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<List<CommentDTO>> getCommentsForMessage(@PathVariable UUID messageId,
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CommentDTO> comments = commentService.getCommentsForMessage(messageId, userDetails.getId());
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}




