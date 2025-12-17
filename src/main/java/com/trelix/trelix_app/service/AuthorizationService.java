package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.*;
import com.trelix.trelix_app.enums.ProjectRole;
import com.trelix.trelix_app.enums.TeamRole;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TeamUserRepository teamUserRepository;
    private final ProjectMemberRepository projectMemberRepository;
//    private final TaskMemberRepository taskMemberRepository;
//    private final TaskRepository taskRepository;
//    private final MessageRepository messageRepository;
//    private final ChannelRepository channelRepository;

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
            throw new AccessDeniedException("You do not have permission to perform this action. Only the team owner can.");
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

//    public void checkProjectAccess(UUID teamId, UUID projectId, UUID userId) {
//        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsMemberInProject(projectId, userId) && !checkIfUserIsAdminInProject(projectId, userId)) {
//            throw new AccessDeniedException("User does not have access to this project.");
//        }
//    }
//
//    public void checkTeamAccess(UUID teamId, UUID userId) {
//        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsMemberInTeam(teamId, userId)) {
//            throw new AccessDeniedException("User does not have access to this team.");
//        }
//    }

//    public void checkTaskAccess(UUID teamId, UUID projectId, UUID taskId, UUID userId) {
//        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsMemberInTask(taskId, userId) && !checkIfUserIsAdminInProject(projectId, userId)) {
//            throw new AccessDeniedException("User does not have access to this task.");
//        }
//    }

//    private boolean checkIfUserIsMemberInTask(UUID taskId, UUID userId) {
//        return taskMemberRepository.findByTaskIdAndUserId(taskId, userId).isPresent();
//    }
//
//    public void checkProjectAdminAccess(UUID teamId, UUID projectId, UUID userId) {
//        if (!checkIfUserIsAdminInTeam(teamId, userId) && !checkIfUserIsAdminInProject(projectId, userId)) {
//            throw new AccessDeniedException("User does not have admin access to this project.");
//        }
//    }

//    // Helper to get task and then check access
//    public void checkTaskAccessByTaskId(UUID taskId, UUID userId) {
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
//
//        checkTaskAccess(task.getProject().getTeam().getId(),
//                task.getProject().getId(),
//                taskId,
//                userId);
//    }

    // Check access to a channel (member of project/team or admin)
//    public void checkChannelAccess(Channel channel, UUID userId) {
//        Project project = channel.getProject();
//        Team team = channel.getTeam();
//
//        if (project != null) {
//            if (!checkIfUserIsMemberInProject(project.getId(), userId) && !checkIfUserIsAdminInProject(project.getId(), userId) && (team != null && !checkIfUserIsAdminInTeam(team.getId(), userId))) {
//                throw new AccessDeniedException("User does not have access to this project channel.");
//            }
//        } else if (team != null) {
//            checkTeamAccess(team.getId(), userId);
//        } else {
//            // Ad-hoc channel without team/project - maybe public or specific access rules
//            // For now, deny if no team/project and no specific rule
//            throw new AccessDeniedException("Access denied to ad-hoc channel.");
//        }
    }

    // Check admin access for a channel
//    public void checkChannelAdminAccess(Channel channel, UUID userId) {
//        Project project = channel.getProject();
//        Team team = channel.getTeam();
//
//        if (project != null) {
//            if (!checkIfUserIsAdminInProject(project.getId(), userId) && (team != null && !checkIfUserIsAdminInTeam(team.getId(), userId))) {
//                throw new AccessDeniedException("User does not have admin access to update this channel.");
//            }
//        } else if (team != null) {
//            checkIfUserIsAdminInTeam(team.getId(), userId);
//        } else {
//            throw new AccessDeniedException("Admin access denied to ad-hoc channel.");
//        }
//    }
//
//    // Verify if the user is the owner of a message
//    public boolean verifyMessageOwner(Message message, UUID userId) {
//        return message.getSender().getId().equals(userId);
//    }
//
//    // Check permissions for deleting a message
//    public void checkMessageDeleteAccess(Message message, UUID userId) {
//        if (verifyMessageOwner(message, userId)) return; // Owner can delete
//
//        Channel channel = message.getChannel();
//        Project project = channel.getProject();
//        Team team = channel.getTeam();
//
//        // Admins of project or team can delete
//        if (project != null && checkIfUserIsAdminInProject(project.getId(), userId)) return;
//        if (team != null && checkIfUserIsAdminInTeam(team.getId(), userId)) return;
//
//        throw new AccessDeniedException("You do not have permission to delete this message.");
//    }

//    // Verify if the user is the owner of a comment
//    public boolean verifyCommentOwner(Comment comment, UUID userId) {
//        return comment.getUser().getId().equals(userId);
//    }
//
//    // Check permissions for deleting a comment
//    public void checkCommentDeleteAccess(Comment comment, UUID userId) {
//        if (verifyCommentOwner(comment, userId)) return; // Owner can delete
//
//        // Check if admin of associated task's project or message's channel's team
//        if (comment.getTask() != null) {
//            Project project = comment.getTask().getProject();
//            if (checkIfUserIsAdminInProject(project.getId(), userId)) return;
//            if (checkIfUserIsAdminInTeam(project.getTeam().getId(), userId)) return;
//        } else if (comment.getMessage() != null) {
//            Channel channel = comment.getMessage().getChannel();
//            Project project = channel.getProject();
//            Team team = channel.getTeam();
//            if (project != null && checkIfUserIsAdminInProject(project.getId(), userId)) return;
//            if (team != null && checkIfUserIsAdminInTeam(team.getId(), userId)) return;
//        }
//
//        throw new AccessDeniedException("You do not have permission to delete this comment.");
//    }
//
//    // Check access to an event
//    public void checkEventAccess(Event event, UUID userId) {
//        if (event.getCreatedBy().getId().equals(userId)) return; // Creator can access
//
//        if (event.getTask() != null) {
//            checkTaskAccessByTaskId(event.getTask().getId(), userId);
//        } else if (event.getProject() != null) {
//            checkProjectAccess(event.getProject().getTeam().getId(), event.getProject().getId(), userId);
//        } else if (event.getTeam() != null) {
//            checkTeamAccess(event.getTeam().getId(), userId);
//        } else {
//            throw new AccessDeniedException("Access denied to event.");
//        }
//    }

    // Check permissions for creating/updating an event
//    public void checkEventCreationAccess(Team team, Project project, Task task, UUID userId) {
//        if (task != null) {
//            checkTaskAccessByTaskId(task.getId(), userId); // User must have access to the task
//        } else if (project != null) {
//            checkProjectAdminAccess(project.getTeam().getId(), project.getId(), userId); // User must be project admin
//        } else if (team != null) {
//            if (!checkIfUserIsAdminInTeam(team.getId(), userId)) {
//                throw new AccessDeniedException("Only team admins can create team-level events.");
//            }
//        } else {
//            throw new AccessDeniedException("Event must be associated with a team, project, or task.");
//        }
//    }

