package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.TeamRole;
import jakarta.validation.constraints.NotNull;

public record UpdateTeamMemberRoleRequest(
    @NotNull(message = "Role cannot be null.")
    TeamRole role
) {}
