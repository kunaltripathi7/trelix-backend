package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.MessageRequestDTO;
import com.trelix.trelix_app.dto.MessageSummaryDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.MessageCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageCommentController {

    private final MessageCommentService messageCommentService;

    @PostMapping("/messages/{messageId}/comments")
    public ResponseEntity<MessageSummaryDTO> addCommentToMessage(@PathVariable UUID messageId,
                                                                 @Valid @RequestBody MessageRequestDTO commentRequest,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageSummaryDTO messageComment = messageCommentService.addCommentToMessage(messageId, commentRequest, userDetails.getId());
        return ResponseEntity.ok(messageComment);
    }

    @GetMapping("/messages/{messageId}/comments")
    public ResponseEntity<List<MessageSummaryDTO>> getCommentsForMessage(@PathVariable UUID messageId,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MessageSummaryDTO> comments = messageCommentService.getCommentsForMessage(messageId, userDetails.getId());
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<MessageSummaryDTO> updateComment(@PathVariable UUID commentId,
                                                           @Valid @RequestBody MessageRequestDTO commentRequest,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageSummaryDTO updatedComment = messageCommentService.updateComment(commentId, commentRequest, userDetails.getId());
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        messageCommentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
