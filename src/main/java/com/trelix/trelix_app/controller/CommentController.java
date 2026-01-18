package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.request.CreateCommentRequest;
import com.trelix.trelix_app.dto.request.UpdateCommentRequest;
import com.trelix.trelix_app.dto.response.CommentResponse;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Comments", description = "Comments on tasks and messages")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Create comment", description = "Add a comment to a task or message")
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResponse createdComment = commentService.createComment(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Get task comments", description = "Get all comments on a task")
    public ResponseEntity<List<CommentResponse>> getCommentsForTask(@PathVariable UUID taskId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CommentResponse> comments = commentService.getCommentsForTask(taskId, userDetails.getId());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/messages/{messageId}")
    @Operation(summary = "Get message comments", description = "Get all comments on a message (thread replies)")
    public ResponseEntity<List<CommentResponse>> getCommentsForMessage(@PathVariable UUID messageId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CommentResponse> comments = commentService.getCommentsForMessage(messageId, userDetails.getId());
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Edit comment", description = "Edit a comment's content. Only the author can edit.")
    public ResponseEntity<CommentResponse> editComment(@PathVariable UUID commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResponse updatedComment = commentService.updateComment(commentId, request, userDetails.getId());
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment", description = "Delete a comment. Only the author can delete.")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
