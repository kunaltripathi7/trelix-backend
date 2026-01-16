package com.trelix.trelix_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.trelix.trelix_app.entity.ProjectMember;
import com.trelix.trelix_app.enums.ProjectRole;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
                projectMember.getUser().getName(),
                projectMember.getUser().getEmail(),
                projectMember.getRole(),
                projectMember.getCreatedAt()
        );
    }
}




