package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Start Time of event is required")
    private LocalDateTime startTime;
    @NotNull(message = "End Time of event is required")
    private LocalDateTime endTime;
    @NotBlank(message = "TeamId is required")
    private String teamId;
    private String projectId;
    private String taskId;
}
