package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.Channel;
import com.trelix.trelix_app.entity.ChannelMember;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ChannelDetailResponse(
        UUID id,
        UUID teamId,
        String teamName,
        UUID projectId,
        String projectName,
        String name,
        String type, // Derived: "TEAM", "PROJECT", "AD_HOC"
        LocalDateTime createdAt,
        List<ChannelMemberResponse> members, // For ad-hoc only
        Integer memberCount // For team/project channels
) {
    public static ChannelDetailResponse from(Channel channel, String teamName, String projectName, List<ChannelMember> channelMembers) {
        String channelType;
        if (channel.getProjectId() != null) {
            channelType = "PROJECT";
        } else if (channel.getTeamId() != null) {
            channelType = "TEAM";
        } else {
            channelType = "AD_HOC";
        }

        List<ChannelMemberResponse> memberResponses = null;
        Integer count = null;

        if ("AD_HOC".equals(channelType)) {
            memberResponses = channelMembers.stream()
                    .map(ChannelMemberResponse::from)
                    .collect(Collectors.toList());
        } else {
            count = channelMembers.size(); 
        }

        return new ChannelDetailResponse(
                channel.getId(),
                channel.getTeamId(),
                teamName,
                channel.getProjectId(),
                projectName,
                channel.getName(),
                channelType,
                channel.getCreatedAt(),
                memberResponses,
                count
        );
    }
}
