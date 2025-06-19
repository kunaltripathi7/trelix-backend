package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.Channel;
import com.trelix.trelix_app.enums.Role;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ChannelRepository;
import com.trelix.trelix_app.repository.ProjectMemberRepository;
import com.trelix.trelix_app.repository.TaskMemberRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class  AuthorizationService {

    private final TeamUserRepository teamUserRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskMemberRepository taskMemberRepository;
    private final ChannelRepository channelRepository;

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
            throw new AccessDeniedException("User does not have access to this project");
        }

    }

    public void checkTeamAccess(UUID teamId, UUID userId) {
        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsMemberInTeam(teamId, userId)) {
            throw new AccessDeniedException("User does not have access to this team");
        }
    }

    public void checkTaskAccess(UUID teamId, UUID projectId, UUID taskId, UUID userId) {
        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsMemberInTask(taskId, userId) && !checkIfUserIsAdminInProject(projectId, userId)) {
            throw new AccessDeniedException("User does not have access to this task");
        }
    }


    public boolean hasEventCreationAccess(UUID teamId, UUID projectId, UUID taskId, UUID userId) {
        if (teamId == null) return false;
        if (checkIfUserIsAdminInTeam(teamId, userId)) return true;
        if (projectId != null && checkIfUserIsAdminInProject(projectId, userId)) return true;
        if (taskId != null && checkIfUserIsMemberInTask(projectId, userId)) return true;
        return false;
    }

    public boolean hasEventAccess(UUID teamId, UUID projectId, UUID taskId, UUID userId) {
        if (teamId == null) return false;
        if (checkIfUserIsAdminInTeam(teamId, userId)) return true;
        if (taskId != null && (checkIfUserIsMemberInTask(projectId, userId) || checkIfUserIsMemberInTask(taskId, userId))) return true;
        if (projectId != null && (checkIfUserIsAdminInProject(projectId, userId) || checkIfUserIsMemberInProject(projectId, userId))) return true;
        if (checkIfUserIsMemberInTeam(teamId, userId)) return true;
        return false;
    }

    private boolean checkIfUserIsMemberInTask(UUID taskId, UUID userId) {
        return taskMemberRepository.findByTaskIdAndUserId(taskId, userId).isPresent();
    }

    public void checkProjectAdminAccess(UUID teamId, UUID projectId, UUID userId) {
        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsAdminInProject(projectId, userId)) {
            throw new AccessDeniedException("User does not have access to this project");
        }
    }

    public void checkMessageAccess(UUID channelId, UUID teamId, UUID projectId,  UUID userId) {
        if (projectId == null) {
             if (!(checkIfUserIsMemberInTeam(teamId, userId) || checkIfUserIsAdminInTeam(teamId, userId))) throw new AccessDeniedException("User doesn't have access to access messages of the channel" + channelId);
        }
        else if (!(checkIfUserIsMemberInProject(projectId, userId) || checkIfUserIsAdminInProject(projectId, userId))) throw new AccessDeniedException("User doesn't have access to access messages of the channel" + channelId);
    }

    public void verifyMessageOwner(UUID senderId, UUID userId) {
        if (!senderId.equals(userId)) throw new AccessDeniedException("User doesn't have permission to edit this message.");
    }

    public void deleteMessageAccess(UUID senderId, UUID userId, UUID teamId, UUID projectId) {
        if (checkIfUserIsAdminInTeam(teamId, userId) || (projectId != null && checkIfUserIsAdminInProject(projectId, userId))) return;
        verifyMessageOwner(senderId, userId);
    }
}