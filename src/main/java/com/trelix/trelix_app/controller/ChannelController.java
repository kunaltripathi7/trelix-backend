package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.ChannelDTO;
import com.trelix.trelix_app.dto.ChannelRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.ChannelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private AuthorizationService authorizationService;


    @PostMapping("/teams/{teamId}/channels")
    public ResponseEntity<ChannelDTO> createChannel(@PathVariable UUID teamId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @Valid @RequestBody ChannelRequest channelRequest) {
        if (channelRequest.getProjectId() == null) {
            authorizationService.checkIfUserIsAdminInTeam(teamId, userDetails.getId());
        } else {
            authorizationService.checkProjectAdminAccess(teamId, channelRequest.getProjectId(), userDetails.getId());
        }
        ChannelDTO createdChannel = channelService.createChannel(teamId, channelRequest.getProjectId(), channelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @GetMapping("/teams/{teamId}/channels/{channelId}")
    public ResponseEntity<ChannelDTO> getChannel(@PathVariable UUID teamId,
                                                 @RequestParam(required = false) UUID projectId,
                                                 @PathVariable UUID channelId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (projectId == null) {
            authorizationService.checkTeamAccess(teamId, userDetails.getId());
        } else {
            authorizationService.checkProjectAccess(teamId, projectId, userDetails.getId());
        }
        ChannelDTO channel = channelService.getChannel(channelId);
        return ResponseEntity.ok(channel);
    }

    @GetMapping("/teams/{teamId}}/channels")
    public ResponseEntity<List<ChannelDTO>> getChannels(@PathVariable UUID teamId,
                                                        @RequestParam(required = false) UUID projectId,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (projectId == null) {
        authorizationService.checkTeamAccess(teamId, userDetails.getId());
        return ResponseEntity.ok(channelService.getChannelsForTeam(teamId));
        } else {
            authorizationService.checkProjectAccess(teamId, projectId, userDetails.getId());
            return ResponseEntity.ok(channelService.getChannelsForProject(projectId));
        }
    }

    @PutMapping("/teams/{teamId}/channels/{channelId}")
    public ResponseEntity<ChannelDTO> updateChannel(@PathVariable UUID teamId,
                                                    @PathVariable UUID channelId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @Valid @RequestBody ChannelRequest channelRequest) {
        if (channelRequest.getProjectId() == null) {
            authorizationService.checkIfUserIsAdminInTeam(teamId, userDetails.getId());
        } else {
            authorizationService.checkProjectAdminAccess(teamId, channelRequest.getProjectId(), userDetails.getId());
        }
        ChannelDTO updatedChannel = channelService.updateChannel(channelId, channelRequest);
        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/teams/{teamId}/channels/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID teamId,
                                              @PathVariable UUID channelId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {

        channelService.deleteChannel(teamId, channelId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

}
