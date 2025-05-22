package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.ProjectMember;
import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.enums.Role;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectMemberRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TeamUserRepository teamUserRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public boolean checkIfUserIsAdminInTeam(UUID teamId, UUID userId) {
        return teamUserRepository.findByTeamIdAndUserIdAndRole(teamId, userId, Role.ROLE_ADMIN).isPresent();
    }

    public boolean checkIfUserIsMemberInTeam(UUID teamId, UUID userId) {
        return teamUserRepository.findByTeamIdAndUserIdAndRole(teamId, userId, Role.ROLE_MEMBER).isPresent();
    }

    public boolean checkIfUserIsMemberInProject(UUID projectId, UUID userId) {
        return projectMemberRepository.findByProjectIdAndUserIdAndRole(projectId, userId, Role.ROLE_MEMBER).isPresent();
    }

    public boolean checkIfUserIsAdminInProject(UUID projectId, UUID userId) {
     return projectMemberRepository.findByProjectIdAndUserIdAndRole(projectId, userId, Role.ROLE_ADMIN).isPresent();
    }

    public void checkProjectAccess(UUID teamId, UUID projectId, UUID userId) {
        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsMemberInProject(projectId, userId) && !checkIfUserIsAdminInProject(projectId, userId)) {
            throw new ResourceNotFoundException("User does not have access to this project");
        }
    }

    public void CheckTaskAccess(UUID taskId, UUID userId) {
        if (!projectMemberRepository.existsByTaskIdAndUserId(taskId, userId)) {
            throw new ResourceNotFoundException("User does not have access to this task");
        }
    }
}