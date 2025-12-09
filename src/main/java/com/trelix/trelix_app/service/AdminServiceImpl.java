package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.RoleAssignmentRequest;
import com.trelix.trelix_app.dto.TeamDetailResponse;
import com.trelix.trelix_app.dto.UserResponse;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.TeamRole;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;

    @Override
    @Transactional
    public TeamDetailResponse transferTeamOwnership(UUID teamId, UUID newOwnerId, UUID requesterId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));

        TeamUser currentOwner = team.getTeamUsers().stream()
                .filter(tu -> tu.getRole() == TeamRole.OWNER)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Could not find owner for team: " + teamId));

        if (!currentOwner.getUser().getId().equals(requesterId)) {
            throw new ForbiddenException("Only the current owner can transfer ownership.", ErrorCode.FORBIDDEN);
        }

        User newOwnerUser = userRepository.findById(newOwnerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + newOwnerId));

        TeamUser newOwnerTeamUser = team.getTeamUsers().stream()
                .filter(tu -> tu.getUser().getId().equals(newOwnerId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("New owner is not a member of the team."));

        currentOwner.setRole(TeamRole.ADMIN);
        newOwnerTeamUser.setRole(TeamRole.OWNER);

        teamUserRepository.save(currentOwner);
        teamUserRepository.save(newOwnerTeamUser);

        Team updatedTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found after ownership transfer."));

        return AppMapper.convertToTeamDetailsResponse(updatedTeam);
    }

    @Override
    @Transactional
    public UserResponse assignGlobalRole(RoleAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        user.setRole(request.getRole());
        User updatedUser = userRepository.save(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getCreatedAt()
        );
    }
}
