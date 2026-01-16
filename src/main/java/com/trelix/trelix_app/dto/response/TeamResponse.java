package com.trelix.trelix_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.trelix.trelix_app.entity.Team;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TeamResponse(
    UUID id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TeamResponse from(Team team) { // static factory method to convert to dto
        return new TeamResponse(
            team.getId(),
            team.getName(),
            team.getDescription(),
            team.getCreatedAt(),
            team.getUpdatedAt()
        );
    }
}




