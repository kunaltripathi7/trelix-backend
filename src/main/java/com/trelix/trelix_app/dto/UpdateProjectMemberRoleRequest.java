package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateProjectMemberRoleRequest(
        @NotNull(message = "Project role cannot be null")
        ProjectRole role
) {}
