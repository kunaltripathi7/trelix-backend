package com.trelix.trelix_app.dto.response;

import com.trelix.trelix_app.entity.Message;
import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID channelId,
        UUID senderId,
        String senderName,
        String content,
        boolean isEdited,
        LocalDateTime editedAt, // Nullable
        LocalDateTime createdAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChannel() != null ? message.getChannel().getId() : null,
                message.getSender().getId(),
                message.getSender().getName(),
                message.getContent(),
                message.getEditedAt() != null,
                message.getEditedAt(),
                message.getCreatedAt()
        );
    }
}




