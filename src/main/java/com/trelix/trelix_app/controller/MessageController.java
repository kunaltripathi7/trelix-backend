package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.MessageDetailDTO;
import com.trelix.trelix_app.dto.MessageRequestDTO;
import com.trelix.trelix_app.dto.MessageSummaryDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/{channelId}/messages")
    public ResponseEntity<MessageDetailDTO> createMessage(@PathVariable UUID channelId, @Valid @RequestBody MessageRequestDTO messageRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageDetailDTO message = messageService.createMessage(channelId, messageRequest, userDetails.getId());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{channelId}/messages")
    public ResponseEntity<List<MessageSummaryDTO>> getMessages(@PathVariable UUID channelId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MessageSummaryDTO> messages = messageService.getMessages(channelId, userDetails.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{channelId}/messages/{messageId}")
    public ResponseEntity<MessageDetailDTO> getMessage(@PathVariable UUID messageId, @PathVariable UUID channelId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageDetailDTO message = messageService.getMessage(messageId, channelId, userDetails.getId());
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<MessageDetailDTO> updateMessage(@PathVariable UUID messageId, @RequestBody MessageRequestDTO messageRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageDetailDTO message = messageService.updateMessage(messageId, messageRequest, userDetails.getId());
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{channelId}/messages/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable UUID channelId, @PathVariable UUID messageId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        messageService.deleteMessage(channelId, messageId, userDetails.getId());
        return ResponseEntity.ok("Message Deleted Successfully");
    }

}
