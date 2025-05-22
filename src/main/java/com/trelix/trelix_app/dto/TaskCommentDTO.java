package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotBlank;
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
public class TaskCommentDTO {
    private UUID id;
    @NotBlank(message = "Content cannot be blank")
    private String content;
    @NotBlank(message = "Task ID cannot be blank")
    private UUID authorId;
    @NotBlank(message = "Task ID cannot be blank")
    private String authorName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}