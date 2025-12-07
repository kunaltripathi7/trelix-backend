package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotNull(message = "Team ID cannot be null")
        UUID teamId,

        UUID projectId,

        @NotBlank(message = "Task title cannot be empty")
        @Size(min = 3, max = 200, message = "Task title must be between 3 and 200 characters")
        String title,

        @Size(max = 2000, message = "Task description cannot exceed 2000 characters")
        String description,

        TaskStatus status,

        TaskPriority priority,

        @Future(message = "Due date must be a future date")
        LocalDate dueDate
) {}
