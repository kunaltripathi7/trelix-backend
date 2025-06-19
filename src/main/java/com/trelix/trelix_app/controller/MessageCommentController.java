package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.MessageRequestDTO;
import com.trelix.trelix_app.dto.MessageSummaryDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.MessageCommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class MessageCommentController {

    @Autowired
    private MessageCommentService messageCommentService;


    @PostMapping("/{channelId}/messages/{messageId}/comments")
    public ResponseEntity<MessageSummaryDTO> addCommentToMessage(@PathVariable UUID channelId, @PathVariable UUID messageId, @Valid @RequestBody MessageRequestDTO commentRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageSummaryDTO messageComment = messageCommentService.addCommentToMessage(channelId, messageId, commentRequest, userDetails.getId());
        return ResponseEntity.ok(messageComment);
    }


    @GetMapping("/{channelId}/messages/{messageId}/comments")
    public ResponseEntity<?> getCommentsForMessage(@PathVariable UUID channelId, @PathVariable UUID messageId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(messageCommentService.getCommentsForMessage(channelId, messageId, userDetails.getId()));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<MessageSummaryDTO> updateComment(@PathVariable UUID commentId, @Valid @RequestBody MessageRequestDTO commentRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageSummaryDTO updatedComment = messageCommentService.updateComment(commentId, commentRequest, userDetails.getId());
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        messageCommentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

}
