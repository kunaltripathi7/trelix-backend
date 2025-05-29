package com.trelix.trelix_app.service;

import com.trelix.trelix_app.enums.Role;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectMemberRepository;
import com.trelix.trelix_app.repository.TaskMemberRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TeamUserRepository teamUserRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskMemberRepository taskMemberRepository;

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

    public void checkTaskAccess(UUID teamId, UUID projectId, UUID taskId, UUID userId) {
        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsMemberInTask(taskId, userId) && !checkIfUserIsAdminInProject(projectId, userId)) {
            throw new ResourceNotFoundException("User does not have access to this task");
        }
    }

    private boolean checkIfUserIsMemberInTask(UUID taskId, UUID userId) {
        return taskMemberRepository.findByTaskIdAndUserId(taskId, userId).isPresent();
    }
}