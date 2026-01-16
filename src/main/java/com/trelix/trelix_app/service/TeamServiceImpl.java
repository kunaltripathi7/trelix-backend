package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.*;
import com.trelix.trelix_app.dto.response.*;
import com.trelix.trelix_app.dto.common.*;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.NotificationType;
import com.trelix.trelix_app.enums.TeamRole;
import com.trelix.trelix_app.exception.ConflictException;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.InvalidRequestException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import com.trelix.trelix_app.repository.UserRepository;
import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
        private final AuthorizationService authorizationService;
        private final EntityManager em;
        private final UserService userService;
        private final KafkaProducerService kafkaProducerService;

        @Override
        public Team getTeamById(UUID id) {
                return teamRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Team not founc with the given Id " + id));
        }

        @Override
        @Transactional
        public TeamResponse createTeam(CreateTeamRequest request, UUID creatorId) {
                User creator = userService.findById(creatorId);

                Team team = new Team();
                team.setName(request.name());
                team.setDescription(request.description());

                Team savedTeam = teamRepository.save(team); // created and updated timestamp -> hibernate injects those
                                                            // now() in
                                                            // the sql but then the java objects and db is out of sync.
                                                            // flush
                                                            // and refetch. just send null and let frontend handle it
                // teamRepository.flush();
                // em.refresh(savedTeam);

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
        @Cacheable(value = "teams", key = "#teamId")
        @Transactional(readOnly = true)
        public TeamDetailResponse getTeamDetails(UUID teamId) {
                Team team = teamRepository.findDetailsById(teamId)
                                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
                return TeamDetailResponse.from(team);
        }

        @Override
        @CacheEvict(value = "teams", key = "#teamId")
        @Transactional
        public TeamResponse updateTeam(UUID teamId, UpdateTeamRequest request, UUID requesterId) {
                authorizationService.verifyTeamAdmin(teamId, requesterId);
                Team team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

                team.setName(request.name());
                team.setDescription(request.description());

                Team updatedTeam = teamRepository.save(team);
                return TeamResponse.from(updatedTeam);
        }

        @Override
        @CacheEvict(value = "teams", key = "#teamId")
        @Transactional
        public void deleteTeam(UUID teamId, UUID requesterId) {
                authorizationService.verifyTeamOwner(teamId, requesterId);
                if (!teamRepository.existsById(teamId)) {
                        throw new ResourceNotFoundException("Team not found with id: " + teamId);
                }

                teamRepository.deleteById(teamId);
        }

        @Override
        @Transactional(readOnly = true)
        public List<TeamMemberResponse> getTeamMembers(UUID teamId, UUID requesterId) {
                authorizationService.verifyTeamMembership(teamId, requesterId);
                Team team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

                return teamUserRepository.findById_TeamId(teamId).stream()
                                .map(TeamMemberResponse::from)
                                .collect(Collectors.toList());
        }

        @Override
        @CacheEvict(value = "teams", key = "#teamId")
        @Transactional
        public TeamMemberResponse addMember(UUID teamId, AddTeamMemberRequest request, UUID requesterId) {
                authorizationService.verifyTeamAdmin(teamId, requesterId);
                Team team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

                User memberUser = userService.findById(request.userId());

                if (teamUserRepository.existsById_TeamIdAndId_UserId(teamId, request.userId())) {
                        throw new ConflictException(
                                        "User " + request.userId() + " is already a member of team " + teamId,
                                        ErrorCode.DATABASE_CONFLICT);
                }

                TeamUser.TeamUserId teamUserId = new TeamUser.TeamUserId(memberUser.getId(), team.getId());
                TeamUser newTeamUser = TeamUser.builder()
                                .id(teamUserId)
                                .user(memberUser)
                                .team(team)
                                .role(request.role())
                                .build();

                teamUserRepository.save(newTeamUser);
                kafkaProducerService.sendNotification(new NotificationEvent(
                                request.userId(),
                                requesterId,
                                NotificationType.TEAM_INVITE,
                                "Team Invite",
                                "You have been added to team: " + team.getName(),
                                teamId));

                return TeamMemberResponse.from(newTeamUser);
        }

        @Override
        @CacheEvict(value = "teams", key = "#teamId")
        @Transactional
        public TeamMemberResponse updateMemberRole(UUID teamId, UUID userId, TeamRole newRole, UUID requesterId) {
                authorizationService.verifyTeamOwner(teamId, requesterId);
                Team team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

                TeamUser targetTeamUser = teamUserRepository.findById_TeamIdAndId_UserId(teamId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Member with user ID " + userId + " not found in team " + teamId));

                if (userId.equals(requesterId)) {
                        throw new InvalidRequestException("You cannot change your own role through this endpoint.",
                                        ErrorCode.INVALID_INPUT);
                }

                if (newRole == TeamRole.OWNER) {
                        throw new InvalidRequestException(
                                        "Direct promotion to OWNER is not permitted. Use the dedicated ownership transfer endpoint.",
                                        ErrorCode.INVALID_INPUT);
                }

                targetTeamUser.setRole(newRole);

                teamUserRepository.save(targetTeamUser);

                return TeamMemberResponse.from(targetTeamUser);
        }

        @Override
        @CacheEvict(value = "teams", key = "#teamId")
        @Transactional
        public void removeMember(UUID teamId, UUID userId, UUID requesterId) {
                authorizationService.verifyTeamAdmin(teamId, requesterId);
                Team team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

                TeamUser targetTeamUser = teamUserRepository.findById_TeamIdAndId_UserId(teamId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Member with user ID " + userId + " not found in team " + teamId));

                if (userId.equals(requesterId)) {
                        throw new InvalidRequestException(
                                        "You cannot remove yourself from the team through this endpoint. Please use a dedicated 'leave team' function.",
                                        ErrorCode.INVALID_INPUT);
                }

                if (targetTeamUser.getRole() == TeamRole.OWNER)
                        throw new InvalidRequestException("Cannot remove the OWNER of the team.",
                                        ErrorCode.INVALID_INPUT);

                teamUserRepository.deleteById_TeamIdAndId_UserId(teamId, userId);
        }

        @Override
        @CacheEvict(value = "teams", key = "#teamId")
        @Transactional
        public List<TeamMemberResponse> transferOwnership(UUID teamId, UUID newOwnerId, UUID requesterId) {
                Team team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));

                TeamUser currentOwner = team.getTeamUsers().stream()
                                .filter(tu -> tu.getRole() == TeamRole.OWNER)
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Could not find owner for team: " + teamId));

                if (!currentOwner.getUser().getId().equals(requesterId)) {
                        throw new ForbiddenException("Only the current owner can transfer ownership.",
                                        ErrorCode.FORBIDDEN);
                }

                User newOwnerUser = userService.findById(newOwnerId);

                TeamUser newOwnerTeamUser = team.getTeamUsers().stream()
                                .filter(tu -> tu.getUser().getId().equals(newOwnerId))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "New owner is not a member of the team."));

                currentOwner.setRole(TeamRole.ADMIN);
                newOwnerTeamUser.setRole(TeamRole.OWNER);

                TeamMemberResponse savedOldOwner = TeamMemberResponse.from(teamUserRepository.save(currentOwner));
                TeamMemberResponse savedNewOwner = TeamMemberResponse.from(teamUserRepository.save(newOwnerTeamUser));

                Team updatedTeam = teamRepository.findById(teamId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Team not found after ownership transfer."));

                return List.of(savedOldOwner, savedNewOwner);
        }
}

// why a different endpoint for transferring ownership -> ? Safety, Clarity,
// Different operation, future maintenance




