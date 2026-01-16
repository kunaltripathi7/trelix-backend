package com.trelix.trelix_app.dto.request;

import com.trelix.trelix_app.enums.TeamRole;
import jakarta.validation.constraints.NotNull;

public record UpdateTeamMemberRoleRequest(
    @NotNull(message = "Role cannot be null.")
    TeamRole role
) {}




