package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.request.AddChannelMemberRequest;
import com.trelix.trelix_app.dto.response.ChannelDetailResponse;
import com.trelix.trelix_app.dto.response.ChannelMemberResponse;
import com.trelix.trelix_app.dto.response.ChannelResponse;
import com.trelix.trelix_app.dto.request.CreateChannelRequest;
import com.trelix.trelix_app.dto.request.UpdateChannelRequest;
import com.trelix.trelix_app.enums.ChannelType;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/v1/channels")
@Validated
@RequiredArgsConstructor
@Tag(name = "Channels", description = "Channel management for team/project collaboration and direct messaging")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    @Operation(summary = "Create a channel", description = "Create a team or project channel. Requires either teamId or projectId.")
    public ResponseEntity<ChannelResponse> createChannel(
            @Valid @RequestBody CreateChannelRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ChannelResponse channelResponse = channelService.createChannel(request, currentUser.getId());
        return new ResponseEntity<>(channelResponse, HttpStatus.CREATED);
    }

    @PostMapping("/dm")
    @Operation(summary = "Start a direct message", description = "Create or get existing DM channel with another user. Both users are automatically added as members.")
    public ResponseEntity<ChannelResponse> startDirectMessage(
            @RequestParam @NotNull UUID otherUserId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ChannelResponse channelResponse = channelService.startDirectMessage(otherUserId, currentUser.getId());
        return ResponseEntity.ok(channelResponse);
    }

    @GetMapping
    @Operation(summary = "Get channels", description = "Get channels by teamId, projectId, or type (DIRECT for DMs)")
    public ResponseEntity<List<ChannelResponse>> getChannels(
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) ChannelType type,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<ChannelResponse> channels = channelService.getChannels(teamId, projectId, type, currentUser.getId());
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/{channelId}")
    @Operation(summary = "Get channel details", description = "Get detailed information about a specific channel")
    public ResponseEntity<ChannelDetailResponse> getChannelById(
            @PathVariable @NotNull UUID channelId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ChannelDetailResponse channelDetail = channelService.getChannelById(channelId, currentUser.getId());
        return ResponseEntity.ok(channelDetail);
    }

    @PutMapping("/{channelId}")
    @Operation(summary = "Update channel", description = "Update channel name or description")
    public ResponseEntity<ChannelResponse> updateChannel(
            @PathVariable @NotNull UUID channelId,
            @Valid @RequestBody UpdateChannelRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ChannelResponse updatedChannel = channelService.updateChannel(channelId, request, currentUser.getId());
        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/{channelId}")
    @Operation(summary = "Delete channel", description = "Delete a channel and all its messages")
    public ResponseEntity<Void> deleteChannel(
            @PathVariable @NotNull UUID channelId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        channelService.deleteChannel(channelId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{channelId}/members")
    @Operation(summary = "Get channel members", description = "Get list of members in a channel")
    public ResponseEntity<List<ChannelMemberResponse>> getChannelMembers(
            @PathVariable @NotNull UUID channelId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<ChannelMemberResponse> members = channelService.getChannelMembers(channelId, currentUser.getId());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{channelId}/members")
    @Operation(summary = "Add channel member", description = "Add a user to a channel")
    public ResponseEntity<ChannelMemberResponse> addMember(
            @PathVariable @NotNull UUID channelId,
            @Valid @RequestBody AddChannelMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ChannelMemberResponse newMember = channelService.addMember(channelId, request, currentUser.getId());
        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
    }

    @DeleteMapping("/{channelId}/members/{userId}")
    @Operation(summary = "Remove channel member", description = "Remove a user from a channel")
    public ResponseEntity<Void> removeMember(
            @PathVariable @NotNull UUID channelId,
            @PathVariable @NotNull UUID userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        channelService.removeMember(channelId, userId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
