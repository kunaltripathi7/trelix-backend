package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.ProjectMember;
import com.trelix.trelix_app.enums.ProjectRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectMemberResponse(
        UUID userId,
        String userName,
        String email,
        ProjectRole role,
        LocalDateTime joinedAt
) {
    public static ProjectMemberResponse from(ProjectMember projectMember) {
        return new ProjectMemberResponse(
                projectMember.getUser().getId(),
                projectMember.getUser().getUsername(), // Assuming User entity has getUsername()
                projectMember.getUser().getEmail(),    // Assuming User entity has getEmail()
                ProjectRole.valueOf(projectMember.getRole()), // Convert String role to ProjectRole enum
                projectMember.getCreatedAt()
        );
    }
}
