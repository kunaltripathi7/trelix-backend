package com.trelix.trelix_app.dto.response;

import com.trelix.trelix_app.entity.Comment;
import com.trelix.trelix_app.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private UUID id;
    private UUID taskId;
    private UUID messageId;
    private UserResponse user;
    private String content;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        UserResponse userResponse = new UserResponse(
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getUser().getEmail(),
                comment.getUser().getCreatedAt());

        UUID taskId = comment.getEntityType() == EntityType.TASK ? comment.getEntityId() : null;
        UUID messageId = comment.getEntityType() == EntityType.MESSAGE ? comment.getEntityId() : null;

        return CommentResponse.builder()
                .id(comment.getId())
                .taskId(taskId)
                .messageId(messageId)
                .user(userResponse)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
