package com.trelix.trelix_app.service.authorization;

import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.enums.EntityType;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectMemberRepository;
import com.trelix.trelix_app.repository.TaskMemberRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskAttachmentAccessVerifier implements AttachmentEntityAccessVerifier {

    private final TaskRepository taskRepository;
    private final TaskMemberRepository taskMemberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TeamUserRepository teamUserRepository;

    @Override
    public EntityType getType() {
        return EntityType.TASK;
    }

    @Override
    public void verify(UUID entityId, UUID userId) {
        Task task = taskRepository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + entityId));

        if (taskMemberRepository.existsByIdTaskIdAndIdUserId(task.getId(), userId))
            return;

        if (task.getProjectId() != null) {
            if (projectMemberRepository.findByIdProjectIdAndIdUserId(task.getProjectId(), userId).isPresent())
                return;
        }

        teamUserRepository.findById_TeamIdAndId_UserId(task.getTeamId(), userId)
                .orElseThrow(() -> new AccessDeniedException("You do not have access to this task."));
    }
}
