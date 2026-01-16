package com.trelix.trelix_app.dto.response;

import com.trelix.trelix_app.entity.DirectMessage;
import java.time.LocalDateTime;
import java.util.UUID;

public record DirectMessageResponse(
        UUID id,
        UUID user1Id,
        UUID user2Id,
        String user1Name,
        String user2Name,
        LocalDateTime createdAt
) {
    public static DirectMessageResponse from(DirectMessage dm, String user1Name, String user2Name) {
        return new DirectMessageResponse(
                dm.getId(),
                dm.getUser1().getId(),
                dm.getUser2().getId(),
                user1Name,
                user2Name,
                dm.getCreatedAt()
        );
    }
}




