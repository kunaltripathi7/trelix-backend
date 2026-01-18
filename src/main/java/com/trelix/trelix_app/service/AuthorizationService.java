package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.*;
import com.trelix.trelix_app.enums.EntityType;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.EventEntityType;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.*;
import com.trelix.trelix_app.service.authorization.AttachmentEntityAccessVerifier;
import com.trelix.trelix_app.service.authorization.EventEntityAccessVerifier;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final TeamUserRepository teamUserRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskMemberRepository taskMemberRepository;
    private final TaskRepository taskRepository;

    private final List<EventEntityAccessVerifier> eventEntityVerifiers;
    private final List<AttachmentEntityAccessVerifier> attachmentEntityVerifiers;

    private Map<EventEntityType, EventEntityAccessVerifier> eventVerifierRegistry;
    private Map<EntityType, AttachmentEntityAccessVerifier> attachmentVerifierRegistry;

    @PostConstruct
    public void initRegistries() {
        eventVerifierRegistry = eventEntityVerifiers.stream()
                .collect(Collectors.toMap(EventEntityAccessVerifier::getType, Function.identity()));
        attachmentVerifierRegistry = attachmentEntityVerifiers.stream()
                .collect(Collectors.toMap(AttachmentEntityAccessVerifier::getType, Function.identity()));
        log.info("Initialized {} event verifiers and {} attachment verifiers",
                eventVerifierRegistry.size(), attachmentVerifierRegistry.size());
    }

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

    public void verifyEntityAccess(EventEntityType entityType, UUID entityId, UUID userId) {
        EventEntityAccessVerifier verifier = eventVerifierRegistry.get(entityType);
        if (verifier == null) {
            throw new BadRequestException("Unsupported entity type: " + entityType, ErrorCode.INVALID_INPUT);
        }
        verifier.verify(entityId, userId);
    }

    public void verifyEntityAccess(EntityType entityType, UUID entityId, UUID userId) {
        AttachmentEntityAccessVerifier verifier = attachmentVerifierRegistry.get(entityType);
        if (verifier == null) {
            throw new BadRequestException("Unsupported entity type for attachment: " + entityType,
                    ErrorCode.INVALID_INPUT);
        }
        verifier.verify(entityId, userId);
    }

    public void verifyAttachmentDeletion(Attachment attachment, UUID userId) {
        if (!attachment.getUploadedBy().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this attachment.");
        }
    }

    public void verifyEventModification(Event event, UUID userId) {
        if (!event.getCreatedBy().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to modify this event.");
        }
    }
}
