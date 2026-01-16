package com.trelix.trelix_app.dto.request;

import com.trelix.trelix_app.enums.TaskPriority;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateTaskRequest(
        @NotBlank(message = "Task title cannot be empty")
        @Size(min = 3, max = 200, message = "Task title must be between 3 and 200 characters")
        String title,

        @Size(max = 2000, message = "Task description cannot exceed 2000 characters")
        String description,

        @NotNull(message = "Task priority cannot be null")
        TaskPriority priority,

        @FutureOrPresent(message = "Due date must be a future date")
        LocalDate dueDate
) {}




