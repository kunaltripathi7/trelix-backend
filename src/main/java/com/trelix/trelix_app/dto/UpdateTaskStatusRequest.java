package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull(message = "Task status cannot be null")
        TaskStatus status
) {}
