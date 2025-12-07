package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.Team;

import java.time.LocalDateTime;
import java.util.UUID;

public record TeamSearchResponse(
        UUID id,
        String name,
        String description,
        int memberCount,
        LocalDateTime createdAt
) {
    public static TeamSearchResponse from(Team team, int memberCount) {
        return new TeamSearchResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                memberCount,
                team.getCreatedAt()
        );
    }
}
