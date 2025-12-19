package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskStatus;
import com.trelix.trelix_app.validation.EitherTeamOrProject;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@EitherTeamOrProject
public record CreateTaskRequest(
        UUID teamId,

        UUID projectId,

        @NotBlank(message = "Task title cannot be empty")
        @Size(min = 3, max = 200, message = "Task title must be between 3 and 200 characters")
        String title,

        @Size(max = 2000, message = "Task description cannot exceed 2000 characters")
        String description,

        TaskStatus status,

        TaskPriority priority,

        @FutureOrPresent(message = "Due date must be today or a future date")
        LocalDate dueDate
) {}
