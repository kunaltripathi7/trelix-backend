package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.Project;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        UUID teamId,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTeamId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
