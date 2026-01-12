package com.trelix.trelix_app.dto;

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
public class CommentDTO {
    private UUID id;
    private UUID taskId;
    private UUID messageId;
    private UserResponse user;
    private String content;
    private LocalDateTime createdAt;
}
