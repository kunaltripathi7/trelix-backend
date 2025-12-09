package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/teams")
@RequiredArgsConstructor
@Validated
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @Valid @RequestBody CreateTeamRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TeamResponse newTeam = teamService.createTeam(request, currentUser.getId());
        return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeamsForUser(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<TeamResponse> teams = teamService.getTeamsForUser(currentUser.getId());
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponse> getTeamById(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TeamDetailResponse team = teamService.getTeamById(teamId, currentUser.getId());
        return ResponseEntity.ok(team);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable UUID teamId,
            @Valid @RequestBody UpdateTeamRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TeamResponse updatedTeam = teamService.updateTeam(teamId, request, currentUser.getId());
        return ResponseEntity.ok(updatedTeam);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        teamService.deleteTeam(teamId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<TeamMemberResponse> members = teamService.getTeamMembers(teamId, currentUser.getId());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamMemberResponse> addMember(
            @PathVariable UUID teamId,
            @Valid @RequestBody AddTeamMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TeamMemberResponse newMember = teamService.addMember(teamId, request, currentUser.getId());
        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
    }

    @PutMapping("/{teamId}/members/{userId}")
    public ResponseEntity<TeamMemberResponse> updateMemberRole(
            @PathVariable UUID teamId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateTeamMemberRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TeamMemberResponse updatedMember = teamService.updateMemberRole(teamId, userId, request.role(), currentUser.getId());
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID teamId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        teamService.removeMember(teamId, userId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/transfer-ownership")
    public ResponseEntity<TeamDetailResponse> transferOwnership(
            @PathVariable UUID teamId,
            @Valid @RequestBody TransferOwnershipRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TeamDetailResponse updatedTeamDetails = teamService.transferOwnership(teamId, request.newOwnerId(), currentUser.getId());
        return ResponseEntity.ok(updatedTeamDetails);
    }
}
