package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.*;
import com.trelix.trelix_app.enums.Role;
import com.trelix.trelix_app.repository.*;
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
    private final TaskRepository taskRepository;
    private final MessageRepository messageRepository;


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

    private boolean checkIfUserIsMemberInTask(UUID taskId, UUID userId) {
        return taskMemberRepository.findByTaskIdAndUserId(taskId, userId).isPresent();
    }

    public void checkProjectAdminAccess(UUID teamId, UUID projectId, UUID userId) {
        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsAdminInProject(projectId, userId)) {
            throw new AccessDeniedException("User does not have access to this project");
        }
    }

    public void checkMessageAccess(Channel channel, UUID userId) {
        Project project = channel.getProject();
        if (project == null) {
            checkTeamAccess(channel.getTeam().getId(), userId);
        } else {
            checkProjectAccess(project.getTeam().getId(), project.getId(), userId);
        }
    }

    public void checkTaskAccessByTaskId(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        checkTaskAccess(task.getProject().getTeam().getId(),
                task.getProject().getId(),
                taskId,
                userId);
    }

    public void checkMessageAccessByMessageId(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        Channel channel = message.getChannel();
        checkMessageAccess(channel, userId);
    }


    public void verifyMessageOwner(UUID senderId, UUID userId) {
        if (!senderId.equals(userId)) throw new AccessDeniedException("User doesn't have permission to edit this message.");
    }
    
    public void deleteMessageAccess(MessageComment comment, UUID userId) {
        Channel channel = comment.getMessage().getChannel();
        Project project = channel.getProject(); // This can be null
        Team team = channel.getTeam();

        if (checkIfUserIsAdminInTeam(team.getId(), userId) || (project != null && checkIfUserIsAdminInProject(project.getId(), userId))) return;

        verifyMessageOwner(comment.getUser().getId(), userId);
    }

    public void checkChannelAccess(Channel channel, UUID userId) {
        Project project = channel.getProject();
        Team team = channel.getTeam();

        if (project != null) {
            if (!checkIfUserIsMemberInProject(project.getId(), userId) && !checkIfUserIsAdminInProject(project.getId(), userId) && !checkIfUserIsAdminInTeam(team.getId(), userId)) {
                throw new AccessDeniedException("User does not have access to this project channel.");
            }
        } else {
            checkTeamAccess(team.getId(), userId);
        }

    }

    public void checkChannelAdminAccess(Channel channel, UUID userId) {
        Project project = channel.getProject();
        Team team = channel.getTeam();
        if (project != null) {
            if (!checkIfUserIsAdminInProject(project.getId(), userId) && !checkIfUserIsAdminInTeam(team.getId(), userId)) {
                throw new AccessDeniedException("User doesn't have Access to update this channel.");
            }
        } else {
            checkIfUserIsAdminInTeam(team.getId(), userId);
        }
    }


    public void checkEventAccess(Event event, UUID userId) {
        if (event.getCreatedBy().getId().equals(userId)) return;
        if (event.getTask() != null) {
            checkTaskAccessByTaskId(event.getTask().getId(), userId);
        } else if (event.getProject() != null) {
            checkProjectAccess(event.getProject().getTeam().getId(), event.getProject().getId(), userId);
        } else {
            checkTeamAccess(event.getTeam().getId(), userId);
        }
    }

    public void checkEventCreationAccess(Team team, Project project, Task task, UUID userId) {
        if (task != null) {
            checkProjectAccess(task.getProject().getTeam().getId(), task.getProject().getId(), userId);
        } else if (project != null) {
            checkProjectAccess(project.getTeam().getId(), project.getId(), userId);
        } else if (team != null) {
            if (!checkIfUserIsAdminInTeam(team.getId(), userId)) {
                throw new AccessDeniedException("Only team admins can create team-level events.");
            }
        } else {
            throw new AccessDeniedException("Event must be associated with a team, project, or task.");
        }
    }
}