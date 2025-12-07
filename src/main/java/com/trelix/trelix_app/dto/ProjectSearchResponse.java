package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.Project;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectSearchResponse(
        UUID id,
        UUID teamId,
        String teamName,
        String name,
        String description,
        int taskCount,
        LocalDateTime createdAt
) {
    public static ProjectSearchResponse from(Project project, String teamName, int taskCount) {
        return new ProjectSearchResponse(
                project.getId(),
                project.getTeam().getId(),
                teamName,
                project.getName(),
                project.getDescription(),
                taskCount,
                project.getCreatedAt()
        );
    }
}
