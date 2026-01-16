package com.trelix.trelix_app.dto.request;

import com.trelix.trelix_app.enums.TeamRole;
import com.trelix.trelix_app.validation.NotOwner;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddTeamMemberRequest(
    @NotNull(message = "User ID cannot be null.")
    UUID userId,

    @NotNull(message = "Role cannot be null.")
    @NotOwner
    TeamRole role
) {}




