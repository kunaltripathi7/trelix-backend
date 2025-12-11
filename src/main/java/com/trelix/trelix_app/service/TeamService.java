package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.enums.TeamRole;

import java.util.List;
import java.util.UUID;

public interface TeamService {

    TeamResponse createTeam(CreateTeamRequest request, UUID creatorId);

    List<TeamResponse> getTeamsForUser(UUID userId);

    TeamDetailResponse getTeamById(UUID teamId, UUID requesterId);

    TeamResponse updateTeam(UUID teamId, UpdateTeamRequest request, UUID requesterId);

    void deleteTeam(UUID teamId, UUID requesterId);

    List<TeamMemberResponse> getTeamMembers(UUID teamId, UUID requesterId);

    TeamMemberResponse addMember(UUID teamId, AddTeamMemberRequest request, UUID requesterId);

    TeamMemberResponse updateMemberRole(UUID teamId, UUID userId, TeamRole newRole, UUID requesterId);

    void removeMember(UUID teamId, UUID userId, UUID requesterId);

    List<TeamMemberResponse> transferOwnership(UUID teamId, UUID newOwnerId, UUID requesterId);

}
