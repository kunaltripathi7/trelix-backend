package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.MessageDetailDTO;
import com.trelix.trelix_app.dto.MessageRequestDTO;
import com.trelix.trelix_app.dto.MessageSummaryDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/channels/{channelId}/messages")
    public ResponseEntity<MessageDetailDTO> createMessage(@PathVariable UUID channelId,
                                                          @Valid @RequestBody MessageRequestDTO messageRequest,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageDetailDTO message = messageService.createMessage(channelId, messageRequest, userDetails.getId());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/channels/{channelId}/messages")
    public ResponseEntity<List<MessageSummaryDTO>> getMessages(@PathVariable UUID channelId,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MessageSummaryDTO> messages = messageService.getMessages(channelId, userDetails.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<MessageDetailDTO> getMessage(@PathVariable UUID messageId,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageDetailDTO message = messageService.getMessage(messageId, userDetails.getId());
        return ResponseEntity.ok(message);
    }

    @PutMapping("/messages/{messageId}")
    public ResponseEntity<MessageDetailDTO> updateMessage(@PathVariable UUID messageId,
                                                          @Valid @RequestBody MessageRequestDTO messageRequest,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageDetailDTO message = messageService.updateMessage(messageId, messageRequest, userDetails.getId());
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        messageService.deleteMessage(messageId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
