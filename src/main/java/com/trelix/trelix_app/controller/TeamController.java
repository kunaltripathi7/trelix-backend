package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.MemberDTO;
import com.trelix.trelix_app.dto.TeamDetailsResponse;
import com.trelix.trelix_app.dto.TeamRequest;
import com.trelix.trelix_app.dto.TeamResponse;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.TeamService;
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
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/teams")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest teamRequest,
                                                   @AuthenticationPrincipal CustomUserDetails user) {
        TeamResponse team = teamService.createTeam(teamRequest, user.getId());
        return new ResponseEntity<>(team, HttpStatus.CREATED);
    }

    @PostMapping("/teams/{teamId}/join")
    public ResponseEntity<Void> joinTeam(@PathVariable UUID teamId,
                                         @AuthenticationPrincipal CustomUserDetails user) {
        teamService.joinTeam(teamId, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/teams")
    public ResponseEntity<List<TeamResponse>> getTeamsForUser(@AuthenticationPrincipal CustomUserDetails user) {
        List<TeamResponse> teams = teamService.getTeamsForUser(user.getId());
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<TeamDetailsResponse> getTeam(@PathVariable UUID teamId,
                                                       @AuthenticationPrincipal CustomUserDetails user) {
        TeamDetailsResponse team = teamService.getTeam(teamId, user.getId());
        return ResponseEntity.ok(team);
    }

    @GetMapping("/teams/{teamId}/members")
    public ResponseEntity<List<MemberDTO>> getMembers(@PathVariable UUID teamId,
                                                      @AuthenticationPrincipal CustomUserDetails user) {
        List<MemberDTO> members = teamService.getMembers(teamId, user.getId());
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/teams/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID teamId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        teamService.deleteTeam(teamId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/teams/{teamId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable UUID teamId,
                                             @PathVariable UUID memberId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        teamService.removeMember(teamId, memberId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
