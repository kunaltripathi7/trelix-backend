package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.DirectMessage;
import java.time.LocalDateTime;
import java.util.UUID;

public record DirectMessageDetailResponse(
        UUID id,
        UUID user1Id,
        UUID user2Id,
        String user1Name,
        String user1Email,
        String user2Name,
        String user2Email,
        LocalDateTime createdAt
) {
    public static DirectMessageDetailResponse from(
            DirectMessage dm,
            String user1Name,
            String user1Email,
            String user2Name,
            String user2Email) {
        return new DirectMessageDetailResponse(
                dm.getId(),
                dm.getUser1Id(),
                dm.getUser2Id(),
                user1Name,
                user1Email,
                user2Name,
                user2Email,
                dm.getCreatedAt()
        );
    }
}
