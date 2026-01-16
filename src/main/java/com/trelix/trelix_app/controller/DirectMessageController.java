package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.request.CreateDirectMessageRequest;
import com.trelix.trelix_app.dto.response.DirectMessageConversationResponse;
import com.trelix.trelix_app.dto.response.DirectMessageDetailResponse;
import com.trelix.trelix_app.dto.response.DirectMessageMessageResponse;
import com.trelix.trelix_app.dto.response.DirectMessageResponse;
import com.trelix.trelix_app.dto.request.EditDirectMessageRequest;
import com.trelix.trelix_app.dto.response.PagedDirectMessageResponse;
import com.trelix.trelix_app.dto.request.SendDirectMessageRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.DirectMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/direct-messages")
@Validated
@RequiredArgsConstructor
public class DirectMessageController {

    private final DirectMessageService directMessageService;

    @PostMapping
    public ResponseEntity<DirectMessageResponse> createOrGetDM(
            @Valid @RequestBody CreateDirectMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        DirectMessageResponse dmResponse = directMessageService.createOrGetDirectMessage(currentUser.getId(),
                request.otherUserId());

        return ResponseEntity.ok(dmResponse);
    }

    @GetMapping
    public ResponseEntity<List<DirectMessageConversationResponse>> getAllDMs(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<DirectMessageConversationResponse> conversations = directMessageService
                .getAllDirectMessages(currentUser.getId());
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{dmId}")
    public ResponseEntity<DirectMessageDetailResponse> getDirectMessageById(
            @PathVariable @NotNull UUID dmId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        DirectMessageDetailResponse dmDetail = directMessageService.getDirectMessageById(dmId, currentUser.getId());
        return ResponseEntity.ok(dmDetail);
    }

    @GetMapping("/{dmId}/messages")
    public ResponseEntity<PagedDirectMessageResponse> getMessages(
            @PathVariable @NotNull UUID dmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PagedDirectMessageResponse messages = directMessageService.getMessages(dmId, page, size, currentUser.getId());
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{dmId}/messages")
    public ResponseEntity<DirectMessageMessageResponse> sendMessage(
            @PathVariable @NotNull UUID dmId,
            @Valid @RequestBody SendDirectMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        DirectMessageMessageResponse messageResponse = directMessageService.sendMessage(dmId, request,
                currentUser.getId());
        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{dmId}/messages/{messageId}")
    public ResponseEntity<DirectMessageMessageResponse> editMessage(
            @PathVariable @NotNull UUID dmId,
            @PathVariable @NotNull UUID messageId,
            @Valid @RequestBody EditDirectMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        DirectMessageMessageResponse updatedMessage = directMessageService.editMessage(dmId, messageId, request,
                currentUser.getId());
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{dmId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable @NotNull UUID dmId,
            @PathVariable @NotNull UUID messageId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        directMessageService.deleteMessage(dmId, messageId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}




