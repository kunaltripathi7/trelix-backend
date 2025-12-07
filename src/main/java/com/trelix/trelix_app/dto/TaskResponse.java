package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID teamId,
        UUID projectId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTeamId(),
                task.getProjectId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
