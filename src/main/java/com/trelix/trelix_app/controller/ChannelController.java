//package com.trelix.trelix_app.controller;
//
//import com.trelix.trelix_app.dto.AddChannelMemberRequest;
//import com.trelix.trelix_app.dto.ChannelDetailResponse;
//import com.trelix.trelix_app.dto.ChannelMemberResponse;
//import com.trelix.trelix_app.dto.ChannelResponse;
//import com.trelix.trelix_app.dto.CreateChannelRequest;
//import com.trelix.trelix_app.dto.UpdateChannelRequest;
//import com.trelix.trelix_app.service.ChannelService;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/channels")
//@Validated
//public class ChannelController {
//
//    private final ChannelService channelService;
//
//    public ChannelController(ChannelService channelService) {
//        this.channelService = channelService;
//    }
//
//    /**
//     * Creates a channel (team/project/ad-hoc). User must be team/project member for respective types.
//     * POST /api/v1/channels
//     *
//     * @param request The CreateChannelRequest DTO containing channel details.
//     * @param jwt The JWT token of the authenticated user, used to extract the creator's ID.
//     * @return ResponseEntity with ChannelResponse and HTTP status 201 Created.
//     */
//    @PostMapping
//    public ResponseEntity<ChannelResponse> createChannel(
//            @Valid @RequestBody CreateChannelRequest request,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID creatorId = UUID.fromString(jwt.getSubject()); // Assuming subject is the user ID
//        ChannelResponse channelResponse = channelService.createChannel(request, creatorId);
//        return new ResponseEntity<>(channelResponse, HttpStatus.CREATED);
//    }
//
//    /**
//     * Get channels accessible to user with filters.
//     * GET /api/v1/channels?teamId={teamId}&projectId={projectId}&type={type}
//     *
//     * @param teamId The ID of the team (optional filter).
//     * @param projectId The ID of the project (optional filter).
//     * @param type The type of channel (optional filter: "TEAM", "PROJECT", "AD_HOC").
//     * @param jwt The JWT token of the authenticated user.
//     * @return ResponseEntity with a list of ChannelResponse DTOs and HTTP status 200 OK.
//     */
//    @GetMapping
//    public ResponseEntity<List<ChannelResponse>> getChannels(
//            @RequestParam(required = false) UUID teamId,
//            @RequestParam(required = false) UUID projectId,
//            @RequestParam(required = false) String type,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        List<ChannelResponse> channels = channelService.getChannels(teamId, projectId, type, requesterId);
//        return ResponseEntity.ok(channels);
//    }
//
//    /**
//     * Get channel details (basic info + members for ad-hoc). User must have access.
//     * GET /api/v1/channels/{channelId}
//     *
//     * @param channelId The ID of the channel to retrieve.
//     * @param jwt The JWT token of the authenticated user.
//     * @return ResponseEntity with ChannelDetailResponse DTO and HTTP status 200 OK.
//     */
//    @GetMapping("/{channelId}")
//    public ResponseEntity<ChannelDetailResponse> getChannelById(
//            @PathVariable @NotNull UUID channelId,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        ChannelDetailResponse channelDetail = channelService.getChannelById(channelId, requesterId);
//        return ResponseEntity.ok(channelDetail);
//    }
//
//    /**
//     * Update channel name. Only channel OWNER (ad-hoc) or project/team admin.
//     * PUT /api/v1/channels/{channelId}
//     *
//     * @param channelId The ID of the channel to update.
//     * @param request The UpdateChannelRequest DTO with the new channel name.
//     * @param jwt The JWT token of the authenticated user.
//     * @return ResponseEntity with ChannelResponse DTO and HTTP status 200 OK.
//     */
//    @PutMapping("/{channelId}")
//    public ResponseEntity<ChannelResponse> updateChannel(
//            @PathVariable @NotNull UUID channelId,
//            @Valid @RequestBody UpdateChannelRequest request,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        ChannelResponse updatedChannel = channelService.updateChannel(channelId, request, requesterId);
//        return ResponseEntity.ok(updatedChannel);
//    }
//
//    /**
//     * Delete channel. Only channel OWNER (ad-hoc) or project/team admin.
//     * DELETE /api/v1/channels/{channelId}
//     *
//     * @param channelId The ID of the channel to delete.
//     * @param jwt The JWT token of the authenticated user.
//     * @return ResponseEntity with no content and HTTP status 204 No Content.
//     */
//    @DeleteMapping("/{channelId}")
//    public ResponseEntity<Void> deleteChannel(
//            @PathVariable @NotNull UUID channelId,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        channelService.deleteChannel(channelId, requesterId);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * Get channel members. (Ad-hoc only - team/project channels derive from parent).
//     * GET /api/v1/channels/{channelId}/members
//     *
//     * @param channelId The ID of the channel to get members from.
//     * @param jwt The JWT token of the authenticated user.
//     * @return ResponseEntity with a list of ChannelMemberResponse DTOs and HTTP status 200 OK.
//     */
//    @GetMapping("/{channelId}/members")
//    public ResponseEntity<List<ChannelMemberResponse>> getChannelMembers(
//            @PathVariable @NotNull UUID channelId,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        List<ChannelMemberResponse> members = channelService.getChannelMembers(channelId, requesterId);
//        return ResponseEntity.ok(members);
//    }
//
//    /**
//     * Add member to ad-hoc channel. Only channel OWNER or existing MEMBER with invite permission.
//     * POST /api/v1/channels/{channelId}/members
//     *
//     * @param channelId The ID of the channel to add a member to.
//     * @param request The AddChannelMemberRequest DTO with the user ID and role.
//     * @param jwt The JWT token of the authenticated user.
//     * @return ResponseEntity with ChannelMemberResponse DTO and HTTP status 201 Created.
//     */
//    @PostMapping("/{channelId}/members")
//    public ResponseEntity<ChannelMemberResponse> addMember(
//            @PathVariable @NotNull UUID channelId,
//            @Valid @RequestBody AddChannelMemberRequest request,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        ChannelMemberResponse newMember = channelService.addMember(channelId, request, requesterId);
//        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
//    }
//
//    /**
//     * Remove member from ad-hoc channel. Only channel OWNER or user themselves.
//     * DELETE /api/v1/channels/{channelId}/members/{userId}
//     *
//     * @param channelId The ID of the channel.
//     * @param userId The ID of the member to remove.
//     * @param jwt The JWT token of the authenticated user.
//     * @return ResponseEntity with no content and HTTP status 204 No Content.
//     */
//    @DeleteMapping("/{channelId}/members/{userId}")
//    public ResponseEntity<Void> removeMember(
//            @PathVariable @NotNull UUID channelId,
//            @PathVariable @NotNull UUID userId,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        channelService.removeMember(channelId, userId, requesterId);
//        return ResponseEntity.noContent().build();
//    }
//}
