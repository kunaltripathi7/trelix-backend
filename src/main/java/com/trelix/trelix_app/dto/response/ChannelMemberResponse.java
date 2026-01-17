package com.trelix.trelix_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.trelix.trelix_app.entity.ChannelMember;
import com.trelix.trelix_app.enums.ChannelRole;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChannelMemberResponse(
        UUID userId,
        String userName,
        String email,
        ChannelRole role,
        LocalDateTime joinedAt) {
    public static ChannelMemberResponse from(ChannelMember channelMember) {
        return new ChannelMemberResponse(
                channelMember.getUser().getId(),
                channelMember.getUser().getName(),
                channelMember.getUser().getEmail(),
                channelMember.getRole(),
                channelMember.getCreatedAt());
    }
}
