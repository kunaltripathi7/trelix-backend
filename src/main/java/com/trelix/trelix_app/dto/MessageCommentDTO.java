package com.trelix.trelix_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageCommentDTO {
    private UUID id;
    private String username;
    private UUID userId;
    private String content;
    private LocalDateTime createdAt;
}
