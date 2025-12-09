package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.TeamRole;
import com.trelix.trelix_app.exception.InvalidRequestException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import com.trelix.trelix_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserRepository userRepository;
    private final AdminService adminService;

    @Override
    @Transactional
    public TeamResponse createTeam(CreateTeamRequest request, UUID creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + creatorId));

        Team team = new Team();
        team.setName(request.name());
        team.setDescription(request.description());

        Team savedTeam = teamRepository.save(team);

        TeamUser.TeamUserId teamUserId = new TeamUser.TeamUserId(creator.getId(), savedTeam.getId());
        TeamUser teamUser = TeamUser.builder()
                .id(teamUserId)
                .user(creator)
                .team(savedTeam)
                .role(TeamRole.OWNER)
                .build();

        teamUserRepository.save(teamUser);

        return TeamResponse.from(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsForUser(UUID userId) {
        return teamRepository.findTeamsByUserId(userId).stream()
                .map(TeamResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDetailResponse getTeamById(UUID teamId, UUID requesterId) {
        teamUserRepository.findById_TeamIdAndId_UserId(teamId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this team."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        List<TeamMemberResponse> members = teamUserRepository.findById_TeamId(teamId).stream()
                .map(TeamMemberResponse::from)
                .collect(Collectors.toList());

        return TeamDetailResponse.from(team, members);
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(UUID teamId, UpdateTeamRequest request, UUID requesterId) {
        TeamUser teamUser = teamUserRepository.findById_TeamIdAndId_UserId(teamId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this team."));

        if (!teamUser.getRole().canManageTeam()) {
            throw new AccessDeniedException("You do not have permission to update this team.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        team.setName(request.name());
        team.setDescription(request.description());

        Team updatedTeam = teamRepository.save(team);
        return TeamResponse.from(updatedTeam);
    }

    @Override
    @Transactional
    public void deleteTeam(UUID teamId, UUID requesterId) {
        TeamUser teamUser = teamUserRepository.findById_TeamIdAndId_UserId(teamId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this team."));

        if (!teamUser.getRole().canDeleteTeam()) {
            throw new AccessDeniedException("You do not have permission to delete this team.");
        }


        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with id: " + teamId);
        }

        teamRepository.deleteById(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamMembers(UUID teamId, UUID requesterId) {
        teamUserRepository.findById_TeamIdAndId_UserId(teamId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this team."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        return teamUserRepository.findById_TeamId(teamId).stream()
                .map(TeamMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamMemberResponse addMember(UUID teamId, AddTeamMemberRequest request, UUID requesterId) {
        TeamUser requesterTeamUser = teamUserRepository.findById_TeamIdAndId_UserId(teamId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this team."));

        if (!requesterTeamUser.getRole().canManageTeam()) {
            throw new AccessDeniedException("You do not have permission to add members to this team.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        User memberUser = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));

        if (teamUserRepository.existsById_TeamIdAndId_UserId(teamId, request.userId())) {
            throw new InvalidRequestException("User " + request.userId() + " is already a member of team " + teamId, ErrorCode.INVALID_INPUT);
        }

        TeamUser.TeamUserId teamUserId = new TeamUser.TeamUserId(memberUser.getId(), team.getId());
        TeamUser newTeamUser = TeamUser.builder()
                .id(teamUserId)
                .user(memberUser)
                .team(team)
                .role(request.role())
                .build();

        teamUserRepository.save(newTeamUser);

        return TeamMemberResponse.from(newTeamUser);
    }

    @Override
    @Transactional
    public TeamMemberResponse updateMemberRole(UUID teamId, UUID userId, TeamRole newRole, UUID requesterId) {

        TeamUser requesterTeamUser = teamUserRepository.findById_TeamIdAndId_UserId(teamId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this team."));

        if (!requesterTeamUser.getRole().canDeleteTeam()) {
            throw new AccessDeniedException("You do not have permission to update member roles in this team.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        TeamUser targetTeamUser = teamUserRepository.findById_TeamIdAndId_UserId(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member with user ID " + userId + " not found in team " + teamId));

        if (userId.equals(requesterId)) {
            throw new InvalidRequestException("You cannot change your own role through this endpoint.", ErrorCode.INVALID_INPUT);
        }

        if (newRole == TeamRole.OWNER) {
            throw new InvalidRequestException("Direct promotion to OWNER is not permitted. Use the dedicated ownership transfer endpoint.", ErrorCode.INVALID_INPUT);
        }

        if (targetTeamUser.getRole() == TeamRole.OWNER && newRole != TeamRole.OWNER) {
            long ownerCount = teamUserRepository.countById_TeamIdAndRole(teamId, TeamRole.OWNER);
            if (ownerCount <= 1) {
                throw new InvalidRequestException("Cannot demote the last OWNER of the team.", ErrorCode.INVALID_INPUT);
            }
        }

        targetTeamUser.setRole(newRole);

        teamUserRepository.save(targetTeamUser);

        return TeamMemberResponse.from(targetTeamUser);
    }

    @Override
    @Transactional
    public void removeMember(UUID teamId, UUID userId, UUID requesterId) {
        TeamUser requesterTeamUser = teamUserRepository.findById_TeamIdAndId_UserId(teamId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this team."));

        if (!requesterTeamUser.getRole().canManageTeam()) {
            throw new AccessDeniedException("You do not have permission to remove members from this team.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        TeamUser targetTeamUser = teamUserRepository.findById_TeamIdAndId_UserId(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member with user ID " + userId + " not found in team " + teamId));

        if (userId.equals(requesterId)) {
            throw new InvalidRequestException("You cannot remove yourself from the team through this endpoint. Please use a dedicated 'leave team' function.", ErrorCode.INVALID_INPUT);
        }

        if (targetTeamUser.getRole() == TeamRole.OWNER) {
            long ownerCount = teamUserRepository.countById_TeamIdAndRole(teamId, TeamRole.OWNER);
            if (ownerCount <= 1) {
                throw new InvalidRequestException("Cannot remove the last OWNER of the team.", ErrorCode.INVALID_INPUT);
            }
        }

        teamUserRepository.deleteById_TeamIdAndId_UserId(teamId, userId);
    }

    @Override
    @Transactional
    public TeamDetailResponse transferOwnership(UUID teamId, UUID newOwnerId, UUID requesterId) {
        // Delegate the entire operation to the AdminService
        return adminService.transferTeamOwnership(teamId, newOwnerId, requesterId);
    }
}
