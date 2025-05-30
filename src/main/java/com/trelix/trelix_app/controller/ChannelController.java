package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.ChannelDTO;
import com.trelix.trelix_app.dto.ChannelRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.ChannelService;
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
    public ResponseEntity<ChannelDTO> createChannelForTeam(@PathVariable UUID teamId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestBody ChannelRequest channelRequest) {
        authorizationService.checkIfUserIsAdminInTeam(teamId, userDetails.getId());
        ChannelDTO createdChannel = channelService.createChannelForTeam(teamId, channelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @PostMapping("/teams/{teamId}/projects/{projectId}/channels")
    public ResponseEntity<ChannelDTO> createChannel(@PathVariable UUID teamId,
                                                    @PathVariable UUID projectId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestBody ChannelRequest channelRequest) {
        authorizationService.checkProjectAdminAccess(teamId, projectId, userDetails.getId());
        ChannelDTO createdChannel = channelService.createChannel(teamId, projectId, channelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }




    @GetMapping("/teams/{teamId}/projects/{projectId}/channels/{channelId}")
    public ResponseEntity<ChannelDTO> getChannel(@PathVariable UUID teamId,
                                                 @PathVariable UUID projectId,
                                                 @PathVariable UUID channelId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        authorizationService.checkProjectAccess(teamId, projectId, userDetails.getId());
        ChannelDTO channel = channelService.getChannel(channelId);
        return ResponseEntity.ok(channel);
    }

    @GetMapping("/teams/{teamId}/channels/{channelId}")
    public ResponseEntity<ChannelDTO> getChannelForTeam(@PathVariable UUID teamId,
                                                                @PathVariable UUID channelId,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        authorizationService.checkTeamAccess(teamId, userDetails.getId());
        ChannelDTO channel = channelService.getChannel(channelId);
        return ResponseEntity.ok(channel);
    }

    @GetMapping("/teams/{teamId}/projects/{projectId}/channels")
    public ResponseEntity<List<ChannelDTO>> getChannelsForProject(@PathVariable UUID teamId,
                                                                  @PathVariable UUID projectId,
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        authorizationService.checkProjectAccess(teamId, projectId, userDetails.getId());
        List<ChannelDTO> channels = channelService.getChannelsForProject(projectId);
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/teams/{teamId}/channels")
    public ResponseEntity<List<ChannelDTO>> getChannelsForTeam(@PathVariable UUID teamId,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        authorizationService.checkTeamAccess(teamId, userDetails.getId());
        List<ChannelDTO> channels = channelService.getChannelsForTeam(teamId);
        return ResponseEntity.ok(channels);
    }

    @PutMapping("/teams/{teamId}/projects/{projectId}/channels/{channelId}")
    public ResponseEntity<ChannelDTO> updateChannel(@PathVariable UUID teamId,
                                                    @PathVariable UUID channelId,
                                                    @PathVariable  UUID projectId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestBody ChannelRequest channelRequest) {
        authorizationService.checkProjectAdminAccess(teamId, projectId, userDetails.getId());
        ChannelDTO updatedChannel = channelService.updateChannel(channelId, channelRequest);
        return ResponseEntity.ok(updatedChannel);
    }

    @PutMapping("/teams/{teamId}/channels/{channelId}")
    public ResponseEntity<ChannelDTO> updateChannelForTeam(@PathVariable UUID teamId,
                                                                   @PathVariable UUID channelId,
                                                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                   @RequestBody ChannelRequest channelRequest) {
        authorizationService.checkIfUserIsAdminInTeam(teamId, userDetails.getId());
        ChannelDTO updatedChannel = channelService.updateChannel(channelId, channelRequest);
        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/teams/{teamId}/projects/{projectId}/channels/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID teamId,
                                              @PathVariable UUID projectId,
                                              @PathVariable UUID channelId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        authorizationService.checkProjectAdminAccess(teamId, projectId, userDetails.getId());
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/teams/{teamId}/channels/{channelId}")
    public ResponseEntity<Void> deleteChannelForTeam(@PathVariable UUID teamId,
                                                     @PathVariable UUID channelId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        authorizationService.checkIfUserIsAdminInTeam(teamId, userDetails.getId());
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }

}
