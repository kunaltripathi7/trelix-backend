package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.request.*;
import com.trelix.trelix_app.dto.response.*;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.TeamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Teams", description = "Team management and membership operations")
public class TeamController {

    private final TeamService teamService;
    private final AuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "Create team", description = "Create a new team. Creator becomes the owner.")
    public ResponseEntity<TeamResponse> createTeam(
            @Valid @RequestBody CreateTeamRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TeamResponse newTeam = teamService.createTeam(request, currentUser.getId());
        return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get user's teams", description = "Get all teams the current user is a member of")
    public ResponseEntity<List<TeamResponse>> getTeamsForUser(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<TeamResponse> teams = teamService.getTeamsForUser(currentUser.getId());
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "Get team details", description = "Get detailed information about a team including members and projects")
    public ResponseEntity<TeamDetailResponse> getTeamById(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        authorizationService.verifyTeamMembership(teamId, currentUser.getId());
        TeamDetailResponse team = teamService.getTeamDetails(teamId);
        return ResponseEntity.ok(team);
    }

    @PutMapping("/{teamId}")
    @Operation(summary = "Update team", description = "Update team name or description. Requires admin privileges.")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable UUID teamId,
            @Valid @RequestBody UpdateTeamRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TeamResponse updatedTeam = teamService.updateTeam(teamId, request, currentUser.getId());
        return ResponseEntity.ok(updatedTeam);
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "Delete team", description = "Delete a team and all associated data. Only the owner can delete.")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        teamService.deleteTeam(teamId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/members")
    @Operation(summary = "Get team members", description = "Get list of all members in a team")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<TeamMemberResponse> members = teamService.getTeamMembers(teamId, currentUser.getId());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{teamId}/members")
    @Operation(summary = "Add team member", description = "Add a user to the team. Sends a notification to the added user.")
    public ResponseEntity<TeamMemberResponse> addMember(
            @PathVariable UUID teamId,
            @Valid @RequestBody AddTeamMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TeamMemberResponse newMember = teamService.addMember(teamId, request, currentUser.getId());
        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
    }

    @PutMapping("/{teamId}/members/{userId}")
    @Operation(summary = "Update member role", description = "Change a team member's role. Only owners can change roles.")
    public ResponseEntity<TeamMemberResponse> updateMemberRole(
            @PathVariable UUID teamId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateTeamMemberRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TeamMemberResponse updatedMember = teamService.updateMemberRole(teamId, userId, request.role(),
                currentUser.getId());
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    @Operation(summary = "Remove team member", description = "Remove a user from the team. Admins can remove members.")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID teamId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        teamService.removeMember(teamId, userId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/transfer-ownership")
    @Operation(summary = "Transfer ownership", description = "Transfer team ownership to another member. Only owners can transfer.")
    public ResponseEntity<List<TeamMemberResponse>> transferOwnership(
            @PathVariable UUID teamId,
            @Valid @RequestBody TransferOwnershipRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<TeamMemberResponse> updatedTeamMembers = teamService.transferOwnership(teamId, request.newOwnerId(),
                currentUser.getId());
        return ResponseEntity.ok(updatedTeamMembers);
    }
}
