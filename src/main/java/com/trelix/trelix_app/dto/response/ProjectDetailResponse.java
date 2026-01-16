package com.trelix.trelix_app.dto.response;

import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.ProjectMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProjectDetailResponse(
        UUID id,
        UUID teamId,
        String teamName,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProjectMemberResponse> members
) {
    public static ProjectDetailResponse from(Project project, String teamName, List<ProjectMember> projectMembers) {
        List<ProjectMemberResponse> memberResponses = projectMembers.stream()
                .map(ProjectMemberResponse::from)
                .collect(Collectors.toList());

        return new ProjectDetailResponse(
                project.getId(),
                project.getTeamId(),
                teamName,
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                memberResponses
        );
    }
}




