package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.AddChannelMemberRequest;
import com.trelix.trelix_app.dto.ChannelDetailResponse;
import com.trelix.trelix_app.dto.ChannelMemberResponse;
import com.trelix.trelix_app.dto.ChannelResponse;
import com.trelix.trelix_app.dto.CreateChannelRequest;
import com.trelix.trelix_app.dto.UpdateChannelRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.ChannelService;
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
@RequestMapping("/api/v1/channels")
@Validated
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<ChannelResponse> createChannel(
            @Valid @RequestBody CreateChannelRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ChannelResponse channelResponse = channelService.createChannel(request, currentUser.getId());
        return new ResponseEntity<>(channelResponse, HttpStatus.CREATED);
    }

//    @GetMapping
//    public ResponseEntity<List<ChannelResponse>> getChannels(
//            @RequestParam(required = false) UUID teamId,
//            @RequestParam(required = false) UUID projectId,
//            @RequestParam(required = false) String type,
//            @AuthenticationPrincipal CustomUserDetails currentUser) {
//        List<ChannelResponse> channels = channelService.getChannels(teamId, projectId, type, currentUser.getId());
//        return ResponseEntity.ok(channels);
//    }
//
//    @GetMapping("/{channelId}")
//    public ResponseEntity<ChannelDetailResponse> getChannelById(
//            @PathVariable @NotNull UUID channelId,
//            @AuthenticationPrincipal CustomUserDetails currentUser) {
//        ChannelDetailResponse channelDetail = channelService.getChannelById(channelId, currentUser.getId());
//        return ResponseEntity.ok(channelDetail);
//    }
//
//    @PutMapping("/{channelId}")
//    public ResponseEntity<ChannelResponse> updateChannel(
//            @PathVariable @NotNull UUID channelId,
//            @Valid @RequestBody UpdateChannelRequest request,
//            @AuthenticationPrincipal CustomUserDetails currentUser) {
//        ChannelResponse updatedChannel = channelService.updateChannel(channelId, request, currentUser.getId());
//        return ResponseEntity.ok(updatedChannel);
//    }
//
//    @DeleteMapping("/{channelId}")
//    public ResponseEntity<Void> deleteChannel(
//            @PathVariable @NotNull UUID channelId,
//            @AuthenticationPrincipal CustomUserDetails currentUser) {
//        channelService.deleteChannel(channelId, currentUser.getId());
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/{channelId}/members")
//    public ResponseEntity<List<ChannelMemberResponse>> getChannelMembers(
//            @PathVariable @NotNull UUID channelId,
//            @AuthenticationPrincipal CustomUserDetails currentUser) {
//        List<ChannelMemberResponse> members = channelService.getChannelMembers(channelId, currentUser.getId());
//        return ResponseEntity.ok(members);
//    }
//
//    @PostMapping("/{channelId}/members")
//    public ResponseEntity<ChannelMemberResponse> addMember(
//            @PathVariable @NotNull UUID channelId,
//            @Valid @RequestBody AddChannelMemberRequest request,
//            @AuthenticationPrincipal CustomUserDetails currentUser) {
//        ChannelMemberResponse newMember = channelService.addMember(channelId, request, currentUser.getId());
//        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
//    }
//
//    @DeleteMapping("/{channelId}/members/{userId}")
//    public ResponseEntity<Void> removeMember(
//            @PathVariable @NotNull UUID channelId,
//            @PathVariable @NotNull UUID userId,
//            @AuthenticationPrincipal CustomUserDetails currentUser) {
//        channelService.removeMember(channelId, userId, currentUser.getId());
//        return ResponseEntity.noContent().build();
//    }
}
