package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.TeamDetailsResponse;
import com.trelix.trelix_app.dto.TeamMemberDTO;
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
    private final AuthorizationService authorizationService;

    public TeamResponse createTeam(TeamRequest request, UUID userId) {
        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        teamRepository.save(team);

        TeamUser teamUser = TeamUser.builder()
                .team(team)
                .role(Role.ROLE_ADMIN)
                .joinedAt(LocalDateTime.now())
                .build();
        teamUserRepository.save(teamUser);
        return new TeamResponse(team.getId(), team.getName(), team.getDescription());
    }

    public void joinTeam(UUID teamId, UUID userId)  {
        if (teamUserRepository.existsByUserIdAndTeamId(userId, teamId)) return;
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TeamUser teamUser = TeamUser.builder()
                .team(team)
                .user(user)
                .role(Role.ROLE_MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();
        teamUserRepository.save(teamUser);
    }

    public List<TeamResponse> getTeams(UUID userId) {
        List<TeamUser> teamUserList = teamUserRepository.findByUserId(userId);
        return teamUserList.stream().map(teamUser -> {
            Team team = teamUser.getTeam();
           return TeamResponse.builder().id(team.getId()).name(team.getName())
                    .description(team.getDescription()).build();
        }).toList();
    }

    public TeamDetailsResponse getTeam(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found."));
        return TeamDetailsResponse.builder()
                .id(team.getId()).name(team.getName())
                .description(team.getDescription())
                .members(team.getTeamUsers().stream().map(AppMapper::convertToTeamMemberDto).toList())
                .projects(team.getProjects().stream().map(AppMapper::convertToProjectResponse).toList())
                .channels(team.getChannels().stream().map(AppMapper::convertToChannelDto).toList())
                .build();
    }

    public List<TeamMemberDTO> getMembers(UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        return team.getTeamUsers().stream().map(AppMapper::convertToTeamMemberDto).toList();
    }

    public void deleteTeam(UUID teamId) {
        teamRepository.deleteById(teamId);
    }

    public void removeMember(UUID memberId, UUID userId) {
        teamUserRepository.deleteByUserIdAndTeamId(userId, memberId);
    }

}

