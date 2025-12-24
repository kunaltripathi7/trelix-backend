package com.trelix.trelix_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.trelix.trelix_app.entity.Channel;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChannelResponse(
        UUID id,
        UUID teamId,
        UUID projectId,
        String name,
        String type,
        LocalDateTime createdAt
) {
    public static ChannelResponse from(Channel channel) {
        String channelType;
        if (channel.getTeamId() != null && channel.getProjectId() != null) {
            channelType = "PROJECT";
        } else if (channel.getTeamId() != null) {
            channelType = "TEAM";
        } else {
            channelType = "AD_HOC";
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getTeamId(),
                channel.getProjectId(),
                channel.getName(),
                channelType,
                channel.getCreatedAt()
        );
    }
}
