package com.trelix.trelix_app.dto.request;

import com.trelix.trelix_app.enums.TaskRole;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskMemberRoleRequest(
        @NotNull(message = "Task role cannot be null")
        TaskRole role
) {}




