package com.trelix.trelix_app.dto.common;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String teamId;
    private String projectId;
    private String taskId;
    private String createdBy;
    private LocalDateTime createdAt;
}




