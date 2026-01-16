package com.trelix.trelix_app.dto.request;

import com.trelix.trelix_app.enums.TaskRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignTaskMemberRequest(
        @NotNull(message = "User ID cannot be null")
        UUID userId,

        @NotNull(message = "Task role cannot be null")
        TaskRole role
) {}




