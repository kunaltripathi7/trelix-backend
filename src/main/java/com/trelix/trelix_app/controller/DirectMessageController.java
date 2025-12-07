package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.CreateDirectMessageRequest;
import com.trelix.trelix_app.dto.DirectMessageConversationResponse;
import com.trelix.trelix_app.dto.DirectMessageDetailResponse;
import com.trelix.trelix_app.dto.DirectMessageMessageResponse;
import com.trelix.trelix_app.dto.DirectMessageResponse;
import com.trelix.trelix_app.dto.EditDirectMessageRequest;
import com.trelix.trelix_app.dto.PagedDirectMessageResponse;
import com.trelix.trelix_app.dto.SendDirectMessageRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.DirectMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/direct-messages")
@Validated
public class DirectMessageController {

    private final DirectMessageService directMessageService;

    public DirectMessageController(DirectMessageService directMessageService) {
        this.directMessageService = directMessageService;
    }

    /**
     * Create or get an existing DM conversation with another user.
     * Returns conversation id.
     * POST /api/v1/direct-messages
     *
     * @param request The CreateDirectMessageRequest DTO containing the other user's ID.
     * @param userDetails The CustomUserDetails of the authenticated user.
     * @return ResponseEntity with DirectMessageResponse and HTTP status 200 OK (if exists) or 201 Created (if new).
     */
    @PostMapping
    public ResponseEntity<DirectMessageResponse> createOrGetDM(
            @Valid @RequestBody CreateDirectMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID currentUserId = userDetails.getId();
        DirectMessageResponse dmResponse = directMessageService.createOrGetDirectMessage(currentUserId, request.otherUserId());
        // The service layer handles whether it's a new creation or existing retrieval.
        // We'll default to 200 OK for idempotency.
        return ResponseEntity.ok(dmResponse);
    }

    /**
     * Get all DM conversations for authenticated user (sorted by last message).
     * GET /api/v1/direct-messages
     *
     * @param userDetails The CustomUserDetails of the authenticated user.
     * @return ResponseEntity with a list of DirectMessageConversationResponse DTOs and HTTP status 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<DirectMessageConversationResponse>> getAllDMs(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID currentUserId = userDetails.getId();
        List<DirectMessageConversationResponse> conversations = directMessageService.getAllDirectMessages(currentUserId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Get DM conversation details. Only participants can access.
     * GET /api/v1/direct-messages/{dmId}
     *
     * @param dmId The ID of the direct message conversation to retrieve.
     * @param userDetails The CustomUserDetails of the authenticated user.
     * @return ResponseEntity with DirectMessageDetailResponse DTO and HTTP status 200 OK.
     */
    @GetMapping("/{dmId}")
    public ResponseEntity<DirectMessageDetailResponse> getDirectMessageById(
            @PathVariable @NotNull UUID dmId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID requesterId = userDetails.getId();
        DirectMessageDetailResponse dmDetail = directMessageService.getDirectMessageById(dmId, requesterId);
        return ResponseEntity.ok(dmDetail);
    }

    /**
     * Get messages in a DM conversation with pagination. Sort by createdAt ASC.
     * GET /api/v1/direct-messages/{dmId}/messages?page={page}&size={size}
     *
     * @param dmId The ID of the direct message conversation.
     * @param page The page number (optional, default 0).
     * @param size The page size (optional, default 50).
     * @param userDetails The CustomUserDetails of the authenticated user.
     * @return ResponseEntity with PagedDirectMessageResponse DTO and HTTP status 200 OK.
     */
    @GetMapping("/{dmId}/messages")
    public ResponseEntity<PagedDirectMessageResponse> getMessages(
            @PathVariable @NotNull UUID dmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID requesterId = userDetails.getId();
        PagedDirectMessageResponse messages = directMessageService.getMessages(dmId, page, size, requesterId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Send a message in DM conversation. Only participants can send.
     * POST /api/v1/direct-messages/{dmId}/messages
     *
     * @param dmId The ID of the direct message conversation.
     * @param request The SendDirectMessageRequest DTO with the message content.
     * @param userDetails The CustomUserDetails of the authenticated user.
     * @return ResponseEntity with DirectMessageMessageResponse DTO and HTTP status 201 Created.
     */
    @PostMapping("/{dmId}/messages")
    public ResponseEntity<DirectMessageMessageResponse> sendMessage(
            @PathVariable @NotNull UUID dmId,
            @Valid @RequestBody SendDirectMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID senderId = userDetails.getId();
        DirectMessageMessageResponse messageResponse = directMessageService.sendMessage(dmId, request, senderId);
        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }

    /**
     * Edit message in DM. Only sender within 15 minutes.
     * PUT /api/v1/direct-messages/{dmId}/messages/{messageId}
     *
     * @param dmId The ID of the direct message conversation.
     * @param messageId The ID of the message to edit.
     * @param request The EditDirectMessageRequest DTO with the new message content.
     * @param userDetails The CustomUserDetails of the authenticated user.
     * @return ResponseEntity with DirectMessageMessageResponse DTO and HTTP status 200 OK.
     */
    @PutMapping("/{dmId}/messages/{messageId}")
    public ResponseEntity<DirectMessageMessageResponse> editMessage(
            @PathVariable @NotNull UUID dmId,
            @PathVariable @NotNull UUID messageId,
            @Valid @RequestBody EditDirectMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID requesterId = userDetails.getId();
        DirectMessageMessageResponse updatedMessage = directMessageService.editMessage(dmId, messageId, request, requesterId);
        return ResponseEntity.ok(updatedMessage);
    }

    /**
     * Delete message in DM. Only sender.
     * DELETE /api/v1/direct-messages/{dmId}/messages/{messageId}
     *
     * @param dmId The ID of the direct message conversation.
     * @param messageId The ID of the message to delete.
     * @param userDetails The CustomUserDetails of the authenticated user.
     * @return ResponseEntity with no content and HTTP status 204 No Content.
     */
    @DeleteMapping("/{dmId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable @NotNull UUID dmId,
            @PathVariable @NotNull UUID messageId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID requesterId = userDetails.getId();
        directMessageService.deleteMessage(dmId, messageId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
