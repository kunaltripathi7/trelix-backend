package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.ChannelMember;
import com.trelix.trelix_app.enums.ChannelRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChannelMemberResponse(
        UUID userId,
        String userName,
        String email,
        ChannelRole role,
        LocalDateTime joinedAt
) {
    public static ChannelMemberResponse from(ChannelMember channelMember) {
        return new ChannelMemberResponse(
                channelMember.getUser().getId(),
                channelMember.getUser().getUsername(), // Assuming User entity has getUsername()
                channelMember.getUser().getEmail(),    // Assuming User entity has getEmail()
                ChannelRole.valueOf(channelMember.getRole()), // Convert String role to ChannelRole enum
                channelMember.getCreatedAt()
        );
    }
}
