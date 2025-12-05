package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.ChannelDTO;
import com.trelix.trelix_app.dto.ChannelRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/channels")
    public ResponseEntity<ChannelDTO> createChannel(@Valid @RequestBody ChannelRequest channelRequest,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChannelDTO createdChannel = channelService.createChannel(channelRequest, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @GetMapping("/channels/{channelId}")
    public ResponseEntity<ChannelDTO> getChannel(@PathVariable UUID channelId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChannelDTO channel = channelService.getChannel(channelId, userDetails.getId());
        return ResponseEntity.ok(channel);
    }

    @GetMapping("/teams/{teamId}/channels")
    public ResponseEntity<List<ChannelDTO>> getChannelsForTeam(@PathVariable UUID teamId,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChannelDTO> channels = channelService.getChannelsForTeam(teamId, userDetails.getId());
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/projects/{projectId}/channels")
    public ResponseEntity<List<ChannelDTO>> getChannelsForProject(@PathVariable UUID projectId,
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChannelDTO> channels = channelService.getChannelsForProject(projectId, userDetails.getId());
        return ResponseEntity.ok(channels);
    }

    @PutMapping("/channels/{channelId}")
    public ResponseEntity<ChannelDTO> updateChannel(@PathVariable UUID channelId,
                                                    @Valid @RequestBody ChannelRequest channelRequest,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChannelDTO updatedChannel = channelService.updateChannel(channelId, channelRequest, userDetails.getId());
        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/channels/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        channelService.deleteChannel(channelId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
