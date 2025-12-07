package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.Message;
import java.time.LocalDateTime;
import java.util.UUID;

public record DirectMessageMessageResponse(
        UUID id,
        UUID directMessageId,
        UUID senderId,
        String senderName,
        String content,
        boolean isEdited,
        LocalDateTime editedAt, // nullable
        LocalDateTime createdAt
) {
    public static DirectMessageMessageResponse from(Message message, String senderName) {
        return new DirectMessageMessageResponse(
                message.getId(),
                message.getDirectMessageId(),
                message.getSenderId(),
                senderName,
                message.getContent(),
                message.getEditedAt() != null,
                message.getEditedAt(),
                message.getCreatedAt()
        );
    }
}
