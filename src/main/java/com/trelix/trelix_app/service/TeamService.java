package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.MemberDTO;
import com.trelix.trelix_app.dto.TeamDetailsResponse;
import com.trelix.trelix_app.dto.TeamRequest;
import com.trelix.trelix_app.dto.TeamResponse;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.Role;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authService;

    public TeamResponse createTeam(TeamRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        teamRepository.save(team);

        TeamUser teamUser = TeamUser.builder()
                .team(team)
                .user(user)
                .role(Role.ROLE_ADMIN)
                .joinedAt(LocalDateTime.now())
                .build();
        teamUserRepository.save(teamUser);
        return new TeamResponse(team.getId(), team.getName(), team.getDescription());
    }

    public void joinTeam(UUID teamId, UUID userId) {
        if (teamUserRepository.existsByUserIdAndTeamId(userId, teamId)) {
            return;
        }
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        TeamUser teamUser = TeamUser.builder()
                .team(team)
                .user(user)
                .role(Role.ROLE_MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();
        teamUserRepository.save(teamUser);
    }

    public List<TeamResponse> getTeamsForUser(UUID userId) {
        List<TeamUser> teamUsers = teamUserRepository.findByUserId(userId);
        return teamUsers.stream()
                .map(teamUser -> AppMapper.convertToTeamResponse(teamUser.getTeam()))
                .toList();
    }

    public TeamDetailsResponse getTeam(UUID teamId, UUID userId) {
        authService.checkTeamAccess(teamId, userId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        return AppMapper.convertToTeamDetailsResponse(team);
    }

    public List<MemberDTO> getMembers(UUID teamId, UUID userId) {
        authService.checkTeamAccess(teamId, userId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        return team.getTeamUsers().stream().map(AppMapper::convertToTeamMemberDto).toList();
    }

    public void deleteTeam(UUID teamId, UUID userId) {
        if (!authService.checkIfUserIsAdminInTeam(teamId, userId)) {
            throw new AccessDeniedException("You do not have permission to delete this team.");
        }
        teamRepository.deleteById(teamId);
    }

    public void removeMember(UUID teamId, UUID memberId, UUID requestingUserId) {
        if (!authService.checkIfUserIsAdminInTeam(teamId, requestingUserId)) {
            throw new AccessDeniedException("You do not have permission to remove members from this team.");
        }
        teamUserRepository.deleteByUserIdAndTeamId(memberId, teamId);
    }
}
