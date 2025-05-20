package com.trelix.trelix_app.controller;


import com.trelix.trelix_app.dto.TeamDetailsResponse;
import com.trelix.trelix_app.dto.TeamMemberDTO;
import com.trelix.trelix_app.dto.TeamRequest;
import com.trelix.trelix_app.dto.TeamResponse;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.trelix.trelix_app.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;


@RestController
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private AuthorizationService authService;

    @PostMapping("/teams")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest teamRequest, @AuthenticationPrincipal CustomUserDetails user) {
        TeamResponse team = teamService.createTeam(teamRequest, user.getId());
        return new ResponseEntity<>(team, HttpStatus.CREATED);
    }


    @PostMapping("/teams/{id}/join")
    public ResponseEntity<String> joinTeam(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails user) {
        teamService.joinTeam(id, user.getId());
        return new ResponseEntity<>("You have joined the team", HttpStatus.OK);
    }

    @GetMapping("/my/teams")
    public ResponseEntity<List<TeamResponse>> getTeams(@AuthenticationPrincipal CustomUserDetails user) {
        List<TeamResponse> teams = teamService.getTeams(user.getId());
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<TeamDetailsResponse> getTeam(@PathVariable UUID teamId, @AuthenticationPrincipal CustomUserDetails user) {
        authService.checkIfUserIsMemberInTeam(user.getId(), teamId);
        TeamDetailsResponse team = teamService.getTeam( teamId);
       return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @GetMapping("/teams/{id}/members")
    public ResponseEntity<List<TeamMemberDTO>> getMembers(@PathVariable UUID teamId, @AuthenticationPrincipal CustomUserDetails user ) {
        authService.checkIfUserIsMemberInTeam(user.getId(), teamId);
        List<TeamMemberDTO> members = teamService.getMembers(teamId);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable UUID teamId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkIfUserIsAdminInTeam(teamId, userDetails.getId());
        teamService.deleteTeam(teamId);
        return new ResponseEntity<>("Team has been deleted.", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/teams/{id}/members/{userId}")
    public ResponseEntity<String> removeMember(@PathVariable("id") UUID teamId, @PathVariable("userId") UUID memberId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkIfUserIsAdminInTeam(teamId, userDetails.getId());
        teamService.removeMember(memberId, userDetails.getId());
        return new ResponseEntity<>("User has been removed.", HttpStatus.NO_CONTENT);
    }

}
