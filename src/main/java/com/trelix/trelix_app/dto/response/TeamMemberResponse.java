package com.trelix.trelix_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.enums.TeamRole;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TeamMemberResponse(
    UUID userId,
    String userName,
    String email,
    TeamRole role,
    LocalDateTime joinedAt
) {
    public static TeamMemberResponse from(TeamUser teamUser) {
        return new TeamMemberResponse(
            teamUser.getUser().getId(),
            teamUser.getUser().getName(),
            teamUser.getUser().getEmail(),
            teamUser.getRole(),
            teamUser.getCreatedAt()
        );
    }
}




