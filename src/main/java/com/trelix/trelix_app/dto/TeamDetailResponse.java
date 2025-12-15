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
    List<ChannelResponse> channels
    ) {

    public static TeamDetailResponse from(Team team) {
        List<TeamMemberResponse> memberResponses = team.getTeamUsers().stream()
                .map(TeamMemberResponse::from)
                .toList();
        List<ProjectResponse> projectResponses = team.getProjects().stream()
                .map(ProjectResponse::from)
                .toList();

        List<ChannelResponse> channelResponses = team.getChannels().stream()
                .map(ChannelResponse::from)
                .toList();

        return new TeamDetailResponse(
                team.getId(), team.getName(), team.getDescription(), team.getCreatedAt(), team.getUpdatedAt(),
                memberResponses, projectResponses, channelResponses);
    }
}
