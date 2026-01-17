package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.request.EditMessageRequest;
import com.trelix.trelix_app.dto.response.MessageResponse;
import com.trelix.trelix_app.dto.response.PagedMessageResponse;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/messages")
@Validated
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Message history and management (send via WebSocket)")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Get messages", description = "Get paginated message history for a channel")
    public ResponseEntity<PagedMessageResponse> getMessages(
            @RequestParam @NotNull UUID channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PagedMessageResponse messages = messageService.getMessages(channelId, page, size, currentUser.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{messageId}")
    @Operation(summary = "Get message by ID", description = "Get a specific message by its ID")
    public ResponseEntity<MessageResponse> getMessageById(
            @PathVariable @NotNull UUID messageId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        MessageResponse message = messageService.getMessageById(messageId, currentUser.getId());
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{messageId}")
    @Operation(summary = "Edit message", description = "Edit message content. Only the sender can edit.")
    public ResponseEntity<MessageResponse> editMessage(
            @PathVariable @NotNull UUID messageId,
            @Valid @RequestBody EditMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        MessageResponse updatedMessage = messageService.editMessage(messageId, request, currentUser.getId());
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "Delete message", description = "Delete a message. Only the sender can delete.")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable @NotNull UUID messageId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        messageService.deleteMessage(messageId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
