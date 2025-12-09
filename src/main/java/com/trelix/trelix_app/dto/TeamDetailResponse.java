package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.Team;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TeamDetailResponse (
    UUID id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<TeamMemberResponse> members,
    List<ProjectResponse> projects,
    List<ChannelDTO> channels
    ) {

    public static TeamDetailResponse from (Team team, List<TeamMemberResponse> members) {
        return new TeamDetailResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getCreatedAt(),
                team.getUpdatedAt()
        )
    }
}
