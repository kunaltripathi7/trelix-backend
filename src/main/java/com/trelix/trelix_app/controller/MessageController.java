package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.EditMessageRequest;
import com.trelix.trelix_app.dto.MessageResponse;
import com.trelix.trelix_app.dto.PagedMessageResponse;
import com.trelix.trelix_app.dto.SendMessageRequest;
import com.trelix.trelix_app.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List; // Import List for getMessages

@RestController
@RequestMapping("/api/v1/messages")
@Validated
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Send a message to a channel. User must have channel access.
     * POST /api/v1/messages
     *
     * @param request The SendMessageRequest DTO containing message details.
     * @param jwt The JWT token of the authenticated user, used to extract the sender's ID.
     * @return ResponseEntity with MessageResponse and HTTP status 201 Created.
     */
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID senderId = UUID.fromString(jwt.getSubject()); // Assuming subject is the user ID
        MessageResponse messageResponse = messageService.sendMessage(request, senderId);
        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }

    /**
     * Get messages in a channel with pagination. User must have channel access.
     * GET /api/v1/messages?channelId={channelId}&page={page}&size={size}
     *
     * @param channelId The ID of the channel to retrieve messages from.
     * @param page The page number (optional, default 0).
     * @param size The page size (optional, default 50).
     * @param jwt The JWT token of the authenticated user.
     * @return ResponseEntity with PagedMessageResponse DTO and HTTP status 200 OK.
     */
    @GetMapping
    public ResponseEntity<PagedMessageResponse> getMessages(
            @RequestParam @NotNull UUID channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        PagedMessageResponse messages = messageService.getMessages(channelId, page, size, requesterId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get single message by ID. User must have channel access.
     * GET /api/v1/messages/{messageId}
     *
     * @param messageId The ID of the message to retrieve.
     * @param jwt The JWT token of the authenticated user.
     * @return ResponseEntity with MessageResponse DTO and HTTP status 200 OK.
     */
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> getMessageById(
            @PathVariable @NotNull UUID messageId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        MessageResponse message = messageService.getMessageById(messageId, requesterId);
        return ResponseEntity.ok(message);
    }

    /**
     * Edit message content. Only sender within 15 minutes.
     * PUT /api/v1/messages/{messageId}
     *
     * @param messageId The ID of the message to edit.
     * @param request The EditMessageRequest DTO with the new content.
     * @param jwt The JWT token of the authenticated user.
     * @return ResponseEntity with MessageResponse DTO and HTTP status 200 OK.
     */
    @PutMapping("/{messageId}")
    public ResponseEntity<MessageResponse> editMessage(
            @PathVariable @NotNull UUID messageId,
            @Valid @RequestBody EditMessageRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        MessageResponse updatedMessage = messageService.editMessage(messageId, request, requesterId);
        return ResponseEntity.ok(updatedMessage);
    }

    /**
     * Delete message. Only sender or channel admin.
     * DELETE /api/v1/messages/{messageId}
     *
     * @param messageId The ID of the message to delete.
     * @param jwt The JWT token of the authenticated user.
     * @return ResponseEntity with no content and HTTP status 204 No Content.
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable @NotNull UUID messageId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        messageService.deleteMessage(messageId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
