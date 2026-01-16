package com.trelix.trelix_app.dto.request;

import com.trelix.trelix_app.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull(message = "Task status cannot be null")
        TaskStatus status
) {}




