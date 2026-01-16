package com.trelix.trelix_app.dto.request;

import com.trelix.trelix_app.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddProjectMemberRequest(
        @NotNull(message = "User ID cannot be null")
        UUID userId,

        @NotNull(message = "Project role cannot be null")
        ProjectRole role
) {}




