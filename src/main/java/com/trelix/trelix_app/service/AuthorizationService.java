package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.*;
import com.trelix.trelix_app.enums.ProjectRole;
import com.trelix.trelix_app.enums.TeamRole;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TeamUserRepository teamUserRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskMemberRepository taskMemberRepository;
    private final TaskRepository taskRepository;

    public TeamUser verifyTeamMembership(UUID teamId, UUID userId) {
        return teamUserRepository.findById_TeamIdAndId_UserId(teamId, userId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this team."));
    }

    public void verifyTeamAdmin(UUID teamId, UUID userId) {
        TeamUser teamUser = verifyTeamMembership(teamId, userId);
        if (!teamUser.getRole().canManageTeam()) {
            throw new AccessDeniedException("You do not have permission to manage this team.");
        }
    }

    public void verifyTeamOwner(UUID teamId, UUID userId) {
        TeamUser teamUser = verifyTeamMembership(teamId, userId);
        if (!teamUser.getRole().canDeleteTeam()) {
            throw new AccessDeniedException(
                    "You do not have permission to perform this action. Only the team owner can.");
        }
    }

    public ProjectMember verifyProjectMembership(UUID projectId, UUID userId) {
        return projectMemberRepository.findByIdProjectIdAndIdUserId(projectId, userId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this project."));
    }

    public void verifyProjectAdmin(UUID projectId, UUID userId) {
        ProjectMember projectMember = verifyProjectMembership(projectId, userId);
        if (!projectMember.getRole().canManageProject()) {
            throw new AccessDeniedException("You do not have permission to manage this project.");
        }
    }

    public void verifyTaskReadAccess(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        verifyTaskReadAccess(task, userId);
    }

    public void verifyTaskReadAccess(Task task, UUID userId) {
        if (taskMemberRepository.existsByIdTaskIdAndIdUserId(task.getId(), userId))
            return;

        if (task.getProjectId() != null) {
            if (projectMemberRepository.findByIdProjectIdAndIdUserId(task.getProjectId(), userId).isPresent())
                return;
        }

        verifyTeamMembership(task.getTeamId(), userId);
    }

    public void verifyTaskWriteAccess(Task task, UUID userId) {
        Optional<TaskMember> taskMember = taskMemberRepository.findByIdTaskIdAndIdUserId(task.getId(), userId);
        boolean isTaskAdmin = taskMember.map(tm -> tm.getRole().canEditTask()).orElse(false);
        if (isTaskAdmin)
            return;
        if (task.getProjectId() != null) {
            Optional<ProjectMember> projectMember = projectMemberRepository
                    .findByIdProjectIdAndIdUserId(task.getProjectId(), userId);
            boolean isProjectAdmin = projectMember.map(pm -> pm.getRole().canManageProject()).orElse(false);
            if (isProjectAdmin)
                return;
        }
        verifyTeamAdmin(task.getTeamId(), userId);
    }

    public TaskMember verifyTaskMembership(UUID taskId, UUID userId) {
        return taskMemberRepository.findByIdTaskIdAndIdUserId(taskId, userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("You are not a member of this Task with ID: " + taskId));
    }

    public void verifyChannelAccess(UUID channelId, UUID userId) {
        // Implementation logic... need repository access or pass Channel object
        // Since we don't have ChannelRepository here, better to pass the Channel object
        // or ID and fetch it.
        // But circular dependency risk if we inject ChannelRepository?
        // Let's assume we pass the Channel object or logic lives in ChannelServiceImpl
        // for now if repositories are missing.
        // Actually, let's keep it simple and implement validation in ChannelServiceImpl
        // for now to avoid circular deps with AuthorizationService <->
        // ChannelRepository
    }
}
