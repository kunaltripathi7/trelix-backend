package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.*;
import com.trelix.trelix_app.dto.response.*;
import com.trelix.trelix_app.dto.common.*;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.enums.TeamRole;

import java.util.List;
import java.util.UUID;

public interface TeamService {

    Team getTeamById(UUID id);

    TeamResponse createTeam(CreateTeamRequest request, UUID creatorId);

    List<TeamResponse> getTeamsForUser(UUID userId);

    TeamDetailResponse getTeamDetails(UUID teamId);

    TeamResponse updateTeam(UUID teamId, UpdateTeamRequest request, UUID requesterId);

    void deleteTeam(UUID teamId, UUID requesterId);

    List<TeamMemberResponse> getTeamMembers(UUID teamId, UUID requesterId);

    TeamMemberResponse addMember(UUID teamId, AddTeamMemberRequest request, UUID requesterId);

    TeamMemberResponse updateMemberRole(UUID teamId, UUID userId, TeamRole newRole, UUID requesterId);

    void removeMember(UUID teamId, UUID userId, UUID requesterId);

    List<TeamMemberResponse> transferOwnership(UUID teamId, UUID newOwnerId, UUID requesterId);

}




